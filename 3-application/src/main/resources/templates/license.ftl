<!DOCTYPE html>
<html lang="en">
<head>

    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta name="description" content="" />
    <meta name="author" content="" />

    <title>营业执照识别</title>
    <link href="/static/business_license/style.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://cdn.staticfile.org/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"></script>
    <script src="/static/jcanvas.min.js"></script>
    <script src="/static/upload_${uploadType}.js"></script>
</head>
<body style="width:1100px;margin:0 auto;">

<div class="container-fluid" id="main" style="width:100%">

    <header class="blog-header py-3">
        <div class="row flex-nowrap justify-content-between align-items-center">
            <div class="col-4 pt-1">
            </div>
            <div class="col-4 text-center">
                <h3>营业执照识别</h3>
            </div>
            <div class="col-4 d-flex justify-content-end align-items-center">
                <a class="btn btn-sm btn-outline-secondary" style="height:42px;margin-right:5px;padding-top:8px" href="/">返回首页</a>
                <a class="btn btn-sm btn-outline-secondary" target="_blank" href="https://aws.amazon.com/cn/about-aws/select-regions/?sc_channel=PS&sc_campaign=acquisition_CN&sc_publisher=baidu&sc_medium=bz&sc_content=pc&sc_detail=HL&sc_category=pc&sc_segment=test&sc_country=CN&trkCampaign=request_for_pilot_account&trk=baidu-ppc-test"><img src="../static/logo.jpg" /></a>
                <a class="btn btn-sm btn-outline-secondary" style="height:42px;margin-left:5px;padding-top:8px" target="_blank" href="https://github.com/dikers/ocr-template-export">Github</a>
            </div>
        </div>
    </header>


    <div>
            <nav class="navbar navbar-light bg-light">
                <form class="form-inline">
					<#include "upload.ftl">
                </form>
            </nav>

            <div>
            </div>
    </div>

    <div class="row" >
        <div class="col-md-8" class="overflow-auto" id="myCanvasParent" >
            <canvas id="myCanvas" width="600" height="800"
                  style="border:1px solid #000000;">
        </canvas>
        </div>

        <!-- Right Start -->
        <div class="col-md-4">


            <div>
                <span>&nbsp;</span>
            </div>

            <div>
                <table class="table" style="width:700px">
                    <thead class="thead-dark">
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">名称</th>
                        <th scope="col">值</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="(result, index) of result_list" >
                        <th scope="row"><span v-text="index + 1"></span></th>
                        <td><span v-text="result.target_name"></span></td>
                        <td><span v-text="result.value"></span></td>
                    </tr>

                    </tbody>
                </table>
            </div>

            <div>
                <img id="showImg" width="400">
            </div>


        </div>

        <!-- Modal  start -->
        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="myModalLabel">提示信息</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body" id="myModalContent">

                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
<!--                        <button type="button" class="btn btn-primary">Save changes</button>-->
                    </div>
                </div>
            </div>
        </div>
        <!-- Modal  end -->
        <!-- Right End -->
    </div>


</div>

</body>
<script src="/static/business_license/config_file.js"></script>
<script src="/static/util.js"></script>
<script src="/static/business_license/ui.js"></script>
<script src="/static/business_license/extract.js"></script>
<script src="/static/business_license/index.js"></script>
<script src="/static/business_license/utils.js"></script>


</html>