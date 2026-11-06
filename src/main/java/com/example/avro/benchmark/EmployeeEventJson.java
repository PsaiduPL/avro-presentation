package com.example.avro.benchmark;

/** Odpowiednik EmployeeEvent (Avro) wysyłany jako JSON. sentAt w mikrosekundach od 1970-01-01 UTC. */
public record EmployeeEventJson(long id, EmployeeJson employee, long sentAt) {

	public record EmployeeJson(String firstName, int age, String email) {
	}

}
