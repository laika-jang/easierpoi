<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>$Title$</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="css/common.css" />
</head>
<body>
<div id="wrapper" class="container container-lg">
    <!-- 탭 버튼 -->
    <ul class="nav nav-tabs mt-4" id="tab" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="validation-tab" data-bs-toggle="tab" data-bs-target="#validation-tab-pane" type="button" role="tab" aria-controls="validation-tab-pane" aria-selected="false">유효성 검수</button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="userReq-tab" data-bs-toggle="tab" data-bs-target="#userReq-tab-pane" type="button" role="tab" aria-controls="userReq-tab-pane" aria-selected="false">유저 제안</button>
        </li>
        <li class="nav-item active" role="presentation">
            <button class="nav-link active" id="coord-tab" data-bs-toggle="tab" data-bs-target="#coord-tab-pane" type="button" role="tab" aria-controls="coord-tab-pane" aria-selected="true">좌표 보정</button>
        </li>
    </ul>

    <!-- 탭 내용 -->
    <div class="tab-content mb-4" id="tabContent">
        <div class="tab-pane fade" id="validation-tab-pane" role="tabpanel" aria-labelledby="validation-tab" tabindex="0">
            <div class="mt-4 mb-4 bg-body rounded-3">
                <div class="mb-2 form-floating">
                    <input type="text" class="form-control" id="place" name="place" placeholder="상호를 입력하세요" required />
                    <label for="place" class="form-label">지역+상호 (필수)</label>
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
        <div class="tab-pane fade show active" id="coord-tab-pane" role="tabpanel" aria-labelledby="coord-tab" tabindex="0">
            <div class="mt-4 mb-4 bg-body rounded-3">
                <div class="mb-2">
                    <div class="col-auto">
                        <button type="button" class="btn btn-primary" id="get-coord-data">데이터 불러오기</button>
                        <button type="button" class="btn btn-secondary" id="init-data">초기화</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 좌표보정 데이터 -->
    <div id="data-container" class="container container-lg d-none">
        <table class="table table-hover">
            <thead>
            <tr>
                <th scope="col">티켓ID</th>
                <th scope="col">상호</th>
                <th scope="col">주소</th>
                <th scope="col" class="text-center">보정여부</th>
                <th scope="col" class="text-center">특이사항</th>
            </tr>
            </thead>
            <tbody></tbody>
        </table>
    </div>

    <!-- 좌표 보정 모달 -->
    <div class="modal fade" id="dataModal" tabindex="-1" aria-labelledby="dataModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-fullscreen-md-down modal-dialog-centered modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">좌표 보정 도우미</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>
                        <span id="data-modal-lp-id" class="badge text-bg-dark me-1">로컬프로필 ID</span>
                        <b id="data-modal-place">상호</b>
                        <small id="data-modal-category" class="text-body-tertiary ms-2">(카테고리)</small>
                    </p>
                    <small>
                        <span id="data-modal-addr-load">도로명 주소</span><br />
                        <span id="data-modal-addr-num" class="text-body-tertiary">지번 주소</span>
                    </small>
                    <hr />
                    <div id="data-modal-search-result-container" class="d-none"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">창 닫기</button>
                    <button type="button" class="btn btn-primary">다음 행으로 이동</button>
                </div>
            </div>
        </div>
    </div>

</div>
</body>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="js/common.js"></script>
<script type="text/javascript" src="js/validity.js"></script>
<script type="text/javascript" src="js/coordCorr.js"></script>
</html>
