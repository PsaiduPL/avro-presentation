package com.example.avro;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.avro.generated.Employee;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
class EmployeeConsumer {

	// KafkaAvroDeserializer czyta ID schematu z wiadomości, pobiera writer's schema z Schema Registry
	// i dopasowuje dane do reader's schema, czyli wygenerowanej klasy Employee
	@KafkaListener(topics = KafkaConfig.EMPLOYEES_TOPIC)
	void onEmployee(Employee employee) {
		log.info("Odebrano: {}", employee.toString());
	}

}
