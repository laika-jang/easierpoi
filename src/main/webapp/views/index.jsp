<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>$Title$</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="css/common.css" />
</head>
<body>
<div class="container gy-5" style="max-width: 450px;">
    <div class="row mb-3">
        <label for="place" class="form-label small">상호</label>
        <input type="text" class="form-control" id="place" name="place" placeholder="상호를 입력하세요" required />
    </div>
    <div class="row mb-3">
        <label for="place" class="form-label small">도로명주소</label>
        <input type="text" class="form-control" id="addrLoad" name="addrLoad" placeholder="도로명주소를 입력하세요" />
    </div>
    <div class="row mb-3">
        <label for="place" class="form-label small">지번주소</label>
        <input type="text" class="form-control" id="addrNum" name="addrNum" placeholder="지번주소를 입력하세요" />
    </div>
    <div class="row d-grid gap-2 mb-5">
        <button type="button" class="btn btn-primary" id="validity" name="validity">검색</button>
        <button type="button" class="btn btn-secondary" id="initForm" name="initForm">초기화</button>
    </div>
    <div class="row mb-5" id="keywords" style="display: none">
        <label for="keywords" class="form-label small">추가 검색어</label>
        <input type="text" class="form-control mb-2" id="keywords" name="keywords" placeholder="검색어를 입력하세요" required />
        <button type="button" class="btn btn-primary" id="validityAdd" name="validityAdd">검색</button>
    </div>
    <div class="row d-grid gap-2 mb-5">
        <button type="button" class="btn btn-outline-primary" id="test01" name="test01">같은 주소에 동일한 상호 있음</button>
        <button type="button" class="btn btn-outline-secondary" id="test02" name="test02">다른 주소에 동일한 상호 있음</button>
        <button type="button" class="btn btn-outline-secondary" id="test03" name="test03">동일한 상호를 찾지 못함</button>
        <button type="button" class="btn btn-outline-primary" id="test04" name="test04">도로명주소에 지번주소 입력</button>
    </div>
</div>
<div id="result-container" class="container" style="max-width: 450px;"></div>
</body>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
<script src="js/common.js" type="text/javascript"></script>
</html>
