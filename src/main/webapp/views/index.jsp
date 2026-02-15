<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>$Title$</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="css/common.css" />
</head>
<body class=".bg-secondary-subtle">
<div class="container mt-4 mb-4 p-3 bg-body rounded-3" style="max-width: 500px;">
    <div class="row mb-2 form-floating">
        <input type="text" class="form-control" id="place" name="place" placeholder="상호를 입력하세요" required />
        <label for="place" class="form-label">상호 (필수)</label>
        <div class="form-text">;으로 구분하여 여러 개의 검색어를 함께 입력할 수 있습니다</div>
    </div>
    <div class="row mb-2 form-floating">
        <input type="text" class="form-control" id="addr" name="addr" placeholder="주소를 입력하세요" required />
        <label for="addr" class="form-label">주소 (필수)</label>
        <div id="addrNum" class="form-text"></div>
    </div>
    <div class="row">
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
<div id="result-container" class="container d-none" style="max-width: 500px;">
    <div id="result-msg" class="alert alert-secondary" role="alert"></div>
    <div id="result-list">
        <p>검색 결과</p>
        <ul class="list-group"></ul>
    </div>
</div>
</body>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
<script src="js/common.js" type="text/javascript"></script>
</html>
