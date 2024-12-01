package com.example.demo.service;

import com.example.demo.model.Statistics;

public interface ProjectService {
    Statistics calculateStatistics(boolean includeCompleted);
}
