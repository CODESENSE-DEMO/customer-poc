package com.unionplace.customer.statistics.spec;

import com.unionplace.customer.management.spec.CustomerStatus;

public interface CustomerStatisticsService {

    long countByStatus(CustomerStatus status);

    double averageAge();

    void incrementChannelCount(String channelCode);
}
