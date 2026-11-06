package com.example.avro;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import com.example.avro.benchmark.EmployeeEventJson;

@Configuration
public class KafkaConfig {

	public static final String EMPLOYEES_TOPIC = "employees-avro";
	public static final String BENCH_AVRO_TOPIC = "bench-avro";
	public static final String BENCH_JSON_TOPIC = "bench-json";

	@Bean
	NewTopic employeesTopic() {
		return TopicBuilder.name(EMPLOYEES_TOPIC).partitions(1).replicas(1).build();
	}

	@Bean
	NewTopic benchAvroTopic() {
		return TopicBuilder.name(BENCH_AVRO_TOPIC).partitions(1).replicas(1).build();
	}

	@Bean
	NewTopic benchJsonTopic() {
		return TopicBuilder.name(BENCH_JSON_TOPIC).partitions(1).replicas(1).build();
	}

	@Bean
	KafkaTemplate<Object, Object> kafkaTemplate(ProducerFactory<Object, Object> producerFactory) {
		return new KafkaTemplate<>(producerFactory);
	}

	@Bean
	KafkaTemplate<String, EmployeeEventJson> jsonKafkaTemplate(ProducerFactory<Object, Object> producerFactory) {
		Map<String, Object> config = new HashMap<>(producerFactory.getConfigurationProperties());
		return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(config,
				new StringSerializer(), new JacksonJsonSerializer<EmployeeEventJson>().noTypeInfo()));
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, EmployeeEventJson> jsonListenerFactory(
			ConsumerFactory<Object, Object> consumerFactory) {
		Map<String, Object> config = new HashMap<>(consumerFactory.getConfigurationProperties());
		var factory = new ConcurrentKafkaListenerContainerFactory<String, EmployeeEventJson>();
		factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(config,
				new StringDeserializer(), new JacksonJsonDeserializer<>(EmployeeEventJson.class, false)));
		return factory;
	}

}
