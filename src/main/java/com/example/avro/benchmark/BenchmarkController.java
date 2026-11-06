package com.example.avro.benchmark;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.avro.benchmark.BenchmarkRun.Summary;

@RestController
class BenchmarkController {

	private final BenchmarkService benchmarkService;
	private final BenchmarkProperties properties;

	BenchmarkController(BenchmarkService benchmarkService, BenchmarkProperties properties) {
		this.benchmarkService = benchmarkService;
		this.properties = properties;
	}

	@PostMapping("/benchmark")
	List<Summary> run(@RequestParam(required = false) Integer count) throws InterruptedException {
		return benchmarkService.run(count != null ? count : properties.messageCount());
	}

}
