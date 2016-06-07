import json
import os


script_dir = os.path.dirname(__file__)
path = os.path.join(script_dir, "../../config.json")
with open(path, "r") as file:
    info = json.load(file)


def get(key):
    return info[key]
