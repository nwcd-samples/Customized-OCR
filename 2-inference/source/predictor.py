import os
import sys
import boto3
import flask
import json
import shutil
import time,datetime
import random
from deploy.hubserving.ocr_system.module import *

DEBUG = False

# The flask app for serving predictions
app = flask.Flask(__name__)

@app.route('/ping', methods=['GET'])
def ping():
    """Determine if the container is working and healthy. In this sample container, we declare
    it healthy if we can load the model successfully."""
    #health = boto3.client('s3') is not None  # You can insert a health check here

    #status = 200 if health else 404
    status = 200
    return flask.Response(response='\n', status=status, mimetype='application/json')


@app.route('/')
def hello_world():
    return 'PadeleOCR endpoint'


@app.route('/invocations', methods=['POST'])
def invocations():
    data = None
    if flask.request.content_type == 'application/json':
        data = flask.request.data.decode('utf-8')
        data = json.loads(data)
        print("invocations params [{}]".format(data))
    else:
        return flask.Response(response='This predictor only supports JSON data', status=415, mimetype='text/plain')    
    
    bucket = data['bucket']
    tt = time.mktime(datetime.datetime.now().timetuple())
    current_output_dir = os.path.join(init_output_dir, str(int(tt))+str(random.randint(1000,9999)))
    os.mkdir(current_output_dir)
    images=[]
    for image_uri in data['image_uri']:
        download_file_name = image_uri.split('/')[-1]
        download_file_name = os.path.join(current_output_dir, download_file_name)
        s3_client.download_file(bucket, image_uri, download_file_name)
        images.append(download_file_name)
    inference_result = ocr.predict(paths=images)
    shutil.rmtree(current_output_dir)
    
    _payload = json.dumps({'status': 500, 'message': 'OCR failed!'})
    if inference_result:
         _payload = json.dumps(inference_result)

    return flask.Response(response=_payload, status=200, mimetype='application/json')

def str2bool(v):
    return v.lower() in ("yes","true","t","1","y")


#---------------------------------------
init_output_dir = '/opt/ml/output_dir'

if not os.path.exists(init_output_dir):
    os.mkdir(init_output_dir)
else:
    print("-------------init_output_dir ", init_output_dir)

s3_client = boto3.client("s3")
use_gpu=str2bool(os.environ.get("USE_GPU", "False"))
print("entry use_gpu:"+str(use_gpu))
ocr = OCRSystem(use_gpu=use_gpu)
#---------------------------------------


if __name__ == '__main__':
    app.run()