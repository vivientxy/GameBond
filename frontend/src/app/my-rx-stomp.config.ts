import { RxStompConfig } from '@stomp/rx-stomp';
import { environment } from '../environments/environment';

export const myRxStompConfig: RxStompConfig = {
  brokerURL: environment.wsUrl,
  connectHeaders: {
    login: 'guest',
    passcode: 'guest',
  },
  heartbeatIncoming: 0,
  heartbeatOutgoing: 20000,
  reconnectDelay: 200,
};