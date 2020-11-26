# -*- coding:utf-8 -*-
# 该文件原始路径：deploy/hubserving/ocr_system/module.py
from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import os
import sys
sys.path.insert(0, ".")

import argparse
import ast
import copy
import math
import time

from paddle.fluid.core import AnalysisConfig, create_paddle_predictor, PaddleTensor
from PIL import Image
import cv2
import numpy as np
import paddle.fluid as fluid

from tools.infer.utility import base64_to_cv2
from tools.infer.predict_system import TextSystem
import uuid

#原文的logger在启动多个work时会出现冲突
import logging
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
logger.addHandler(logging.StreamHandler(sys.stdout))

class OCRSystem:
    def __init__(self, use_gpu=False, enable_mkldnn=False):
        """
        initialize with the necessary elements
        """
        from deploy.hubserving.ocr_system.params import read_params
        cfg = read_params()

        cfg.use_gpu = use_gpu
        if use_gpu:
            try:
                _places = os.environ["CUDA_VISIBLE_DEVICES"]
                int(_places[0])
                print("use gpu: ", use_gpu)
                print("CUDA_VISIBLE_DEVICES: ", _places)
                cfg.gpu_mem = 8000
            except:
                raise RuntimeError(
                    "Environment Variable CUDA_VISIBLE_DEVICES is not set correctly. If you wanna use gpu, please set CUDA_VISIBLE_DEVICES via export CUDA_VISIBLE_DEVICES=cuda_device_id."
                )
        cfg.ir_optim = True
        cfg.enable_mkldnn = enable_mkldnn

        self.text_sys = TextSystem(cfg)

    def read_images(self, paths=[]):
        images = []
        for img_path in paths:
            assert os.path.isfile(
                img_path), "The {} isn't a valid file.".format(img_path)
            img = cv2.imread(img_path)
            if img is None:
                logger.info("error in loading image:{}".format(img_path))
                continue
            images.append(img)
        return images
        
    def convert(self, dt_boxes, rec_res, width, height,drop_score = 0.5):
        result = {"DocumentMetadata": {"Pages": 1}, "JobStatus": "SUCCEEDED"}
        block_page = {"BlockType": "PAGE",
                      "Geometry": {"BoundingBox": {"Width": 1.0, "Height": 1.0, "Left": 0.0, "Top": 0.0},
                                   "Polygon": [{"X": 0.0, "Y": 0.0}, {"X": 1.0, "Y": 0.0}, {"X": 1.0, "Y": 1.0},
                                               {"X": 0.0, "Y": 1.0}]}, "Id": str(uuid.uuid4())}

        ids = []
        result["Blocks"] = [block_page]
        dt_num = len(dt_boxes)
        for i in range(dt_num):
            text, score = rec_res[i]
            if score < drop_score:
                continue
            block_word = {"BlockType": "WORD"}
            block_word["Confidence"] = str(score)
            block_word["Text"] = text
            current_left=dt_boxes[i][0][0]
            current_top=dt_boxes[i][0][1]
            current_width=dt_boxes[i][1][0]-dt_boxes[i][0][0]
            current_height=dt_boxes[i][2][1]-dt_boxes[i][0][1]
            BoundingBox = {"Width": current_width / width, "Height": current_height / height,
                           "Left": current_left / width, "Top": current_top / height}
            Polygon_0 = {"X": dt_boxes[i][0][0] / width,
                         "Y": dt_boxes[i][0][1] / height}
            Polygon_1 = {"X": dt_boxes[i][1][0] / width,
                         "Y": dt_boxes[i][1][1] / height}
            Polygon_2 = {"X": dt_boxes[i][2][0] / width,
                         "Y": dt_boxes[i][2][1] / height}
            Polygon_3 = {"X": dt_boxes[i][3][0] / width,
                         "Y": dt_boxes[i][3][1] / height}
            Polygon = [Polygon_0, Polygon_1, Polygon_2, Polygon_3]
            block_word["Geometry"] = {"BoundingBox": BoundingBox, "Polygon": Polygon}
            block_word_id = str(uuid.uuid4())
            block_word["Id"] = block_word_id
            block_word["Page"] = 1
            ids.append(block_word_id)
            result["Blocks"].append(block_word)

        block_page["Relationships"] = [{"Type": "CHILD", "Ids": ids}]
        block_page["Page"] = 1
        return result

    def predict(self, images=[], paths=[]):
        """
        Get the chinese texts in the predicted images.
        Args:
            images (list(numpy.ndarray)): images data, shape of each is [H, W, C]. If images not paths
            paths (list[str]): The paths of images. If paths not images
        Returns:
            res (list): The result of chinese texts and save path of images.
        """

        if images != [] and isinstance(images, list) and paths == []:
            predicted_data = images
        elif images == [] and isinstance(paths, list) and paths != []:
            predicted_data = self.read_images(paths)
        else:
            raise TypeError("The input data is inconsistent with expectations.")

        assert predicted_data != [], "There is not any image to be predicted. Please check the input data."

        all_results = []
        for img in predicted_data:
            if img is None:
                logger.info("error in loading image")
                all_results.append([])
                continue
            image = Image.fromarray(cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
            starttime = time.time()
            dt_boxes, rec_res = self.text_sys(img)
            elapse = time.time() - starttime
            logger.info("Predict time: {}".format(elapse))
            all_results.append(self.convert(dt_boxes, rec_res, image.width, image.height))
        return all_results


if __name__ == '__main__':
    ocr = OCRSystem(use_gpu=False)
    image_path = [
        './doc/imgs/11.jpg',
        './doc/imgs/12.jpg',
    ]
    res = ocr.predict(paths=image_path)
    print(res)
