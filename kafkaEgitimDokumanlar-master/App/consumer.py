from kafka import KafkaConsumer
import json

bootstrap_server = "localhost:9092"
topic_name = "ssmdb.yazilim.user-created.0"

consumer = KafkaConsumer(topic_name,
                         bootstrap_servers=bootstrap_server,
                         group_id="myfirstApp",
                         enable_auto_commit=True,
                         auto_offset_reset="earliest"
                         )


def start_consume(cons):
    for msg in cons:
        payload = json.loads(msg.value)
        print("payload", payload)
        # cons.commit()


start_consume(consumer)
