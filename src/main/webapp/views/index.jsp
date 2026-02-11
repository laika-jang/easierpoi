<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>$Title$</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="css/common.css" />
</head>
<body>
<div class="container gy-5" style="max-width: 450px;">
    <div class="row mt-4 mb-2">
        <label for="place" class="form-label col-form-label col-sm-2">상호</label>
        <div class="col-sm-10">
            <input type="text" class="form-control" id="place" name="place" placeholder="상호를 입력하세요" required />
        </div>
    </div>
    <div class="row mb-3">
        <label for="addr" class="form-label col-form-label col-sm-2">주소</label>
        <div class="col-sm-10">
            <input type="text" class="form-control" id="addr" name="addr" placeholder="주소를 입력하세요" required />
            <div id="addrNum" class="form-text"></div>
        </div>
    </div>
    <div class="row mb-4">
        <div class="col-auto">
            <button type="button" class="btn btn-primary" id="validity">&nbsp;검색&nbsp;</button>
            <button type="button" class="btn btn-secondary" id="initForm">초기화</button>
        </div>
        <div class="col-auto">
            <select class="form-select" id="test">
                <option selected>테스트</option>
                <option value="1">있어요 (O)</option>
                <option value="2">없어요 (X)</option>
                <option value="3">다른 주소</option>
            </select>
        </div>
    </div>
</div>
<div id="result-container" class="container" style="max-width: 450px;"></div>
</body>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
<script src="js/common.js" type="text/javascript"></script>
</html>
