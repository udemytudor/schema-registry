As part of our streaming platform capabilities, we want to be able to analyze our users’ activity.
This information will be stored in a Kafka topic called “user-activity”. 
Events will contain the user id (as an integer) and a string that represents the user activity (for instance, “logged in”, “payment registered”, etc).
We expect multiple producers and consumers in the future, so we should enforce a data contract for this topic using an Avro schema.
For the first step, let’s create one producer and one consumer. 
