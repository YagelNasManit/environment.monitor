
# environment.monitor

[![Build Status](https://travis-ci.org/YagelNasManit/environment.monitor.svg?branch=master)](https://travis-ci.org/YagelNasManit/environment.monitor)

Is a tool for continuous gathering, aggregation and representation of environment health, that provides access to environment status information via both UI dashboards & REST API


For example this view and mutch more:

Current Environment states | Environment daily stats  
--- | --- 
![CURRENT STATUS DASHBOARD](https://raw.githubusercontent.com/wiki/YagelNasManit/environment.monitor/images/current_env_status_dashboard.png)| ![CURRENT STATUS DASHBOARD](https://raw.githubusercontent.com/wiki/YagelNasManit/environment.monitor/images/env_daily_status_dashboard.png)
STATUS DASHBOARD](https://raw.githubusercontent.com/wiki/YagelNasManit/environment.monitor/images/resource_details_dashboard.png)|https://raw.githubusercontent.com/wiki/YagelNasManit/environment.monitor/images/resource_details_dashboard_logs.png




# Quickstart
// todo :)

# Current Functionality (1.0-SNAPSHOT)
- current environments status dashboard
- environment last 24h status dashboard
- current environment status service



#add environment monitor artefacts repository with maven:
```xml
...
<repositories>
    <repository>
        <id>Cybercat-mvn-repo</id>
        <url>https://raw.github.com/YagelNasManit/environment.monitor/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
...
```
