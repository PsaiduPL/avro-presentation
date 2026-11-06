package com.example.avro;

import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.avro.generated.Employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
class EmployeeController {

	private final KafkaTemplate<Object, Object> kafka;

	record EmployeeRequest(String firstName, int age, String email) {
	}

	@PostMapping("/employees")
	Map<String, Object> send(@RequestBody EmployeeRequest request) throws Exception {
	    
		Employee employee = new Employee(request.firstName(), request.age(), request.email());

		// KafkaAvroSerializer rejestruje schemat w Schema Registry i dokleja jego ID do wiadomości
		var result = kafka.send(KafkaConfig.EMPLOYEES_TOPIC, request.firstName(), employee).get();
		log.info("Wysłano: {}", employee);
		return Map.of(
				"topic", result.getRecordMetadata().topic(),
				"partition", result.getRecordMetadata().partition(),
				"offset", result.getRecordMetadata().offset(),
				"valueBytes", result.getRecordMetadata().serializedValueSize());
	}

}
