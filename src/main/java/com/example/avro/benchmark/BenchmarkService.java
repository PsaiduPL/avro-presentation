package com.example.avro.benchmark;

import java.time.Instant;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.avro.KafkaConfig;
import com.example.avro.benchmark.BenchmarkRun.Summary;
import com.example.avro.benchmark.EmployeeEventJson.EmployeeJson;
import com.example.avro.generated.Employee;
import com.example.avro.generated.EmployeeEvent;

/**
 * Wysyła ten sam rekord N razy jako Avro i jako JSON.
 * Każda wiadomość niesie czas wysłania (sentAt), a konsument po odebraniu zapisuje czas końca.
 */
@Service
public class BenchmarkService {

	private static final int  WARMUP = 10;
	private static final Employee EMPLOYEE = Employee.newBuilder().setFirstName("Ala").setAge(2).build();
	private static final EmployeeJson EMPLOYEE_JSON = new EmployeeJson("Ala", 2, null);

	private final KafkaTemplate<Object, Object> avroTemplate;
	private final KafkaTemplate<String, EmployeeEventJson> jsonTemplate;

	private volatile BenchmarkRun avroRun = new BenchmarkRun("avro", 0);
	private volatile BenchmarkRun jsonRun = new BenchmarkRun("json", 0);

	BenchmarkService(KafkaTemplate<Object, Object> avroTemplate,
			KafkaTemplate<String, EmployeeEventJson> jsonTemplate) {
		this.avroTemplate = avroTemplate;
		this.jsonTemplate = jsonTemplate;
	}

	public synchronized List<Summary> run(int count) throws InterruptedException {

		runAvro(WARMUP);
		runJson(WARMUP);
		return List.of(runAvro(count), runJson(count));
	}

	private Summary runAvro(int count) throws InterruptedException {
		avroRun = new BenchmarkRun("avro", count);
		for (long id = 0; id < count; id++) {
			avroTemplate.send(KafkaConfig.BENCH_AVRO_TOPIC, new EmployeeEvent(id, EMPLOYEE, Instant.now()));
		}
		return avroRun.awaitSummary();
	}

	private Summary runJson(int count) throws InterruptedException {
		jsonRun = new BenchmarkRun("json", count);
		for (long id = 0; id < count; id++) {
			jsonTemplate.send(KafkaConfig.BENCH_JSON_TOPIC, new EmployeeEventJson(id, EMPLOYEE_JSON, BenchmarkRun.nowMicros()));
		}
		return jsonRun.awaitSummary();
	}

	@KafkaListener(topics = KafkaConfig.BENCH_AVRO_TOPIC, groupId = "benchmark")
	void onAvro(ConsumerRecord<String, EmployeeEvent> record) {
		avroRun.received(BenchmarkRun.toMicros(record.value().getSentAt()), record.serializedValueSize());
	}

	@KafkaListener(topics = KafkaConfig.BENCH_JSON_TOPIC, groupId = "benchmark", containerFactory = "jsonListenerFactory")
	void onJson(ConsumerRecord<String, EmployeeEventJson> record) {
		jsonRun.received(record.value().sentAt(), record.serializedValueSize());
	}

}
