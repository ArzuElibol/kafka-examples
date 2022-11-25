import time
from kafka import KafkaProducer
import json
import uuid

bootstrap_server = "localhost:9092"
topic_name = "ssmdb.yazilim.user-created.0"

"""
def serilestir(message):
    return json.dumps(message).encode('utf-8')
"""


def on_error(exception):
    print("exception", exception)


def on_success(metadata):
    print("metadata", metadata)


def produce_message(my_message):
    producer = KafkaProducer(
        bootstrap_servers=bootstrap_server,
        key_serializer=str.encode,
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )
    producer.send(topic=topic_name,
                  value=my_message,
                  key=str(uuid.uuid4()),
                  headers=[("X-AgentName", b'myapp1')]
                  ).add_callback(on_success).add_errback(on_error)

    print("success")


for i in range(1000, 2000):
    time.sleep(5)
    msg = {
        "id": f"user-{i}",
        "adi": f"Ankara - {i}",
        "soyadi": "Çinçin",
        "yas": 50,
        "kullanici_adi": "cincin"
    }
    produce_message(msg)
