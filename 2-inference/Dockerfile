FROM ubuntu:16.04

ARG PYTHON_VERSION=3.7
# See http://bugs.python.org/issue19846
ENV LANG C.UTF-8
ENV LD_LIBRARY_PATH /opt/conda/lib/:$LD_LIBRARY_PATH
ENV PATH /opt/conda/bin:$PATH

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

RUN apt-get install -y --no-install-recommends nginx net-tools\
    && rm -rf /var/lib/apt/lists/*

RUN curl -L -o ~/miniconda.sh https://nwcd-samples.s3.cn-northwest-1.amazonaws.com.cn/software/miniconda/Miniconda3-latest-Linux-x86_64.sh \
 && chmod +x ~/miniconda.sh \
 && ~/miniconda.sh -b -p /opt/conda \
 && rm ~/miniconda.sh
RUN conda update -n base conda
RUN conda install -y python=$PYTHON_VERSION

RUN pip install flask gevent gunicorn boto3 -i https://opentuna.cn/pypi/web/simple && \
        rm -rf /root/.cache

RUN conda install paddlepaddle==2.0.0rc0 -c paddle 
WORKDIR /opt/ml/code
COPY dockersource ./
RUN pip install -r requirements.txt -i https://opentuna.cn/pypi/web/simple
RUN pip install awscli==1.18.181 -i https://opentuna.cn/pypi/web/simple && aws configure set default.region cn-northwest-1

        
# Set some environment variables. PYTHONUNBUFFERED keeps Python from buffering our standard
# output stream, which means that logs can be delivered to the user quickly. PYTHONDONTWRITEBYTECODE
# keeps Python from writing the .pyc files which are unnecessary in this case. We also update
# PATH so that the train and serve programs are found when the container is invoked.

ENV PYTHONUNBUFFERED=TRUE
ENV PYTHONDONTWRITEBYTECODE=TRUE
ENV PATH="/opt/ml/code/:${PATH}"
#ENV MODEL_SERVER_WORKERS=1
ENV USE_GPU=False

ENTRYPOINT ["python3"]