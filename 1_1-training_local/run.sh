cur_dir=$(cd "$(dirname "$0")"; pwd) 
echo current dir ${cur_dir}
nvidia-docker run -v ${cur_dir}/container/local_test/:/opt/ml/ --shm-size=12g --rm ocr-training-local:rec train
