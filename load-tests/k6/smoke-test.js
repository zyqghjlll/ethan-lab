import http from 'k6/http';
  import { check } from 'k6';

  export const options = {
    vus: 5,
    duration: '30s',
  };

  export default function () {
    const payload = JSON.stringify({
      source: 'order-service',
      eventType: 'ORDER_CREATED',
      identifyKey: `key-${__VU}-${__ITER}`,
      eventBody: JSON.stringify({ orderId: `${__VU}-${__ITER}` }),
      zoneId: 'Asia/Shanghai',
    });

    const res = http.post('http://localhost:9999/event/ingest', payload, {
      headers: { 'Content-Type': 'application/json' },
    });

    check(res, {
      'status is 200': (r) => r.status === 200,
    });
  }