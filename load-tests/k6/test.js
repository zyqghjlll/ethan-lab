import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';

const duplicateRequests = new Counter('duplicate_requests');
const ingestTrend = new Trend('ingest_duration');

export const options = {
  stages: [
    { duration: '30s', target: 50 },
    { duration: '1m',  target: 100 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(99)<500'],
    http_req_failed:   ['rate<0.01'],
    ingest_duration:   ['p(95)<300'],
  },
};

export default function () {

  // 10%概率发送重复key，测试幂等机制
  const isDuplicate = Math.random() < 0.1;
  const identifyKey = isDuplicate
    ? `key-${__VU}-0`
    : `key-${__VU}-${__ITER}`;

  if (isDuplicate) {
    duplicateRequests.add(1);
  }

  const payload = JSON.stringify({
    source:      'order-service',
    eventType:   'ORDER_CREATED',
    identifyKey: identifyKey,
    eventBody:   JSON.stringify({ orderId: `${__VU}-${__ITER}` }),
    zoneId:      'Asia/Shanghai',
  });

  const res = http.post(
    'http://localhost:9999/event/ingest',
    payload,
    { headers: { 'Content-Type': 'application/json' } }
  );

  ingestTrend.add(res.timings.duration);

  check(res, {
    'status is 200':        (r) => r.status === 200,
    'response time < 500ms':(r) => r.timings.duration < 500,
  });

  sleep(0.1);
}