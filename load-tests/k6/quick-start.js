import http from 'k6/http';

export const options = {
    vus: 1,
    iterations: 100
}

const runId = Date.now().toString();

export default function () {
    const eventKey = `${runId}-${__ITER}`;

    const url = 'http://localhost:9999/event/ingest';
    const payload = JSON.stringify({
        source: 'quick-start.js',
        eventType: 'TEST_EVENT',
        identifyKey: eventKey,
        eventBody: JSON.stringify({eventKey}),
        zoneId: 'Asia/Shanghai',
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const req = http.post(url, payload, params);

    console.log(req);
}