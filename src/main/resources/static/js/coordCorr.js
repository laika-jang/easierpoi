setCoordEvents();

function setCoordEvents() {
    // 구글시트 데이터 불러오기
    document.getElementById('get-coord-data').addEventListener('click', coordCorr);
}

// 구글시트 데이터 불러오기
async function coordCorr() {
    const url = `/api/v1/coord-corr/get-data`;

    try {
        const response = await fetch(url);
        const result = await response.json();

        console.log(result);
    } catch (e) {
        console.error(e);
    }
}