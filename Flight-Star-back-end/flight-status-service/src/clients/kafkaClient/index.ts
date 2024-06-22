import { Kafka } from 'kafkajs';

const kafka = new Kafka({
  clientId: 'service-flight-system',
  brokers: ['kafka:9092'],
});

const producer = kafka.producer();

export const connectProducer = async () => {
  await producer.connect();
  console.log('Kafka Producer is connected and ready.');
};

export const sendEmailMessage = async (topic: string, messages: any) => {
  try {
    await producer.send({
      topic,
      messages: [
        {
          value: JSON.stringify(messages),
          headers: {
            'content-type': 'application/json',
            '__TypeId__': 'SendMailMessage',
          },
        },
      ],
    });
    console.log('Message sent successfully');
  } catch (error) {
    console.error('Failed to send message:', error);
  }
};
