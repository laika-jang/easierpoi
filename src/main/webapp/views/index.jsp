<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>$Title$</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="css/common.css" />
</head>
<body>
<div id="wrapper" class="container" style="max-width: 500px;">
    <ul class="nav nav-tabs mt-4" id="tab" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" id="validation-tab" data-bs-toggle="tab" data-bs-target="#validation-tab-pane" type="button" role="tab" aria-controls="validation-tab-pane" aria-selected="true">유효성 검수</button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="userReq-tab" data-bs-toggle="tab" data-bs-target="#userReq-tab-pane" type="button" role="tab" aria-controls="userReq-tab-pane" aria-selected="false">유저 제안</button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="coord-tab" data-bs-toggle="tab" data-bs-target="#coord-tab-pane" type="button" role="tab" aria-controls="coord-tab-pane" aria-selected="false">유저 제안</button>
        </li>
    </ul>
    <div class="tab-content mb-4" id="tabContent">
        <div class="tab-pane fade show active" id="validation-tab-pane" role="tabpanel" aria-labelledby="validation-tab" tabindex="0">
            <div class="mt-4 mb-4 bg-body rounded-3">
                <div class="mb-2 form-floating">
                    <input type="text" class="form-control" id="place" name="place" placeholder="상호를 입력하세요" required />
                    <label for="place" class="form-label">상호 (필수)</label>
                </div>
                <div class="mb-2 form-floating">
                    <input type="text" class="form-control" id="addr" name="addr" placeholder="주소를 입력하세요" required />
                    <label for="addr" class="form-label">주소 (필수)</label>
                    <div id="addr-num" class="form-text"></div>
                </div>
                <div class="row mt-4">
                    <div class="col-auto">
                        <button type="button" class="btn btn-primary" id="validate">&nbsp;검색&nbsp;</button>
                        <button type="button" class="btn btn-secondary" id="init-form">초기화</button>
                    </div>
                </div>
            </div>
            <div id="result-container" class="d-none"></div>
        </div>
        <div class="tab-pane fade" id="userReq-tab-pane" role="tabpanel" aria-labelledby="userReq-tab" tabindex="0"></div>
        <div class="tab-pane fade" id="coord-tab-pane" role="tabpanel" aria-labelledby="coord-tab" tabindex="0">
            <div class="mt-4 mb-4 bg-body rounded-3">
                <div class="mb-2">
                    <input type="file" class="form-control" id="csv-input" name="csv-input" placeholder="상호를 입력하세요" required />
                    <div class="form-text">CSV 데이터 행: 로컬프로필 ID, 상호명, 지번주소, 도로명주소</div>
                </div>
                <div class="row mt-4">
                    <div class="col-auto">
                        <button type="button" class="btn btn-primary" id="submit-csv">데이터 변환</button>
                        <button type="button" class="btn btn-secondary" id="init-data">초기화</button>
                    </div>
                </div>
            </div>
            <div id="data-container" class="d-none"></div>
        </div>
    </div>
</div>
</body>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="js/common.js"></script>
</html>
