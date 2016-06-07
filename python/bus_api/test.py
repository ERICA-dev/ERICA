from AMQSubscriber import AMQSubscriber
from AMQPublisher import AMQPublisher
# import time

amqPublisher = AMQPublisher()
amqSubscriber = AMQSubscriber()


def myFunc(message):
    print message
    amqPublisher.publish("testTopik", "awmagadlooopz")

amqSubscriber.subscribe("testTopik", myFunc)

# this recursion starter won't work cause of thread issue
# example can still be evoked manually from AMQ-frontend...
amqPublisher.publish("testTopik", "fiire")
