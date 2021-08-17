FROM nvidia/cuda:10.1-cudnn7-devel-ubuntu16.04

ARG PYTHON_VERSION=3.7
# See http://bugs.python.org/issue19846
ENV LANG C.UTF-8
ENV LD_LIBRARY_PATH /opt/conda/lib/:$LD_LIBRARY_PATH
ENV PATH /opt/conda/bin:$PATH

COPY 7fa2af80.pub ./
RUN apt-key add 7fa2af80.pub
RUN apt-get update
RUN apt-get install -y --no-install-recommends \
    build-essential \
    ca-certificates \
    cmake \
    curl \
    libgl1-mesa-glx \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender-dev \
    zlib1g-dev

RUN apt-get install -y --no-install-recommends jq unzip

RUN curl -L -o ~/miniconda.sh https://nwcd-samples.s3.cn-northwest-1.amazonaws.com.cn/software/miniconda/Miniconda3-latest-Linux-x86_64.sh \
 && chmod +x ~/miniconda.sh \
 && ~/miniconda.sh -b -p /opt/conda \
 && rm ~/miniconda.sh
RUN conda update -n base conda
RUN conda install -y python=$PYTHON_VERSION

RUN conda install paddlepaddle-gpu==2.0.2 cudatoolkit=10.1 -c paddle
WORKDIR /opt/code
ENV PATH="/opt/code:${PATH}"
COPY dockersource ./
RUN pip install -r requirements.txt -i https://opentuna.cn/pypi/web/simple
COPY ch_ppocr_server_v2.0_rec_pre/ pretrain_models/ch_ppocr_server_v2.0_rec_pre/
COPY rec_chinese_common_train_v2.0.yml ./     
COPY train ./