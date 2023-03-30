# data-sink-app
Reads data from kaka topic and writes it to a cassandra database
It uses Kafka's batch processing listener to consume the messages. There are couple of integration tests written, the current application can process  100k records in around a minute that includes consuming the message of the topic and 
wriiting it to cassandra. There is a very little bit of tuning I have done to achieve this processing time. 
Since the test uses Embedded kafka and cassandra test container as db, the test takes little while to run as it downloads cassandra 3.0 image.
In a commercial environment Scylladb is a much better replacment than Cassandra for higher loads to reduce the latency. Scylladb is 10 times faster than Cassandra and no extra code needs to be written for this
You can simply run the tests from your IDE or run as a mvn clean install.
