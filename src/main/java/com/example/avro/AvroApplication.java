package com.example.avro;

import java.util.Arrays;

import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.util.ClassSecurityValidator;
import org.apache.commons.codec.binary.Hex;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import com.example.avro.generated.Employee;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AvroApplication {

	public static void main(String[] args) throws Exception{
	    System.setProperty("org.apache.avro.SERIALIZABLE_PACKAGES", "com.example.avro.generated");
		SpringApplication.run(AvroApplication.class, args);
	}

}
