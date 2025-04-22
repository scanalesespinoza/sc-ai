// realtime-client.js
export class RealtimeClient {
    constructor({ url, retryDelay = 5000, onMessage, onStatusChange }) {
        this.url = url;
        this.retryDelay = retryDelay;
        this.retryEnabled = true;
        this.ws = null;
        this.reconnectTimer = null;
        this.countdownTimer = null;
        this.messageQueue = [];
        this.onMessage = onMessage || (() => { });
        this.onStatusChange = onStatusChange || (() => { });
        this.countingDown = false;
    }

    get status() {
        if (!this.ws) return 'disconnected';
        switch (this.ws.readyState) {
            case WebSocket.CONNECTING: return 'connecting';
            case WebSocket.OPEN: return 'connected';
            case WebSocket.CLOSING: return 'closing';
            case WebSocket.CLOSED: return 'disconnected';
            default: return 'unknown';
        }
    }

    connect() {
        if (this.ws && this.ws.readyState !== WebSocket.CLOSED) return;

        this.ws = new WebSocket(this.url);
        this.updateStatus();

        this.ws.onopen = () => {
            this.clearTimers();
            this.updateStatus();
            while (this.messageQueue.length > 0) {
                this.ws.send(this.messageQueue.shift());
            }
        };

        this.ws.onmessage = (e) => {
            try {
                const msg = JSON.parse(e.data);
                if (!msg || typeof msg !== 'object') throw new Error("Not a JSON object");
                this.onMessage(msg);
            } catch (err) {
                console.warn("Invalid message", e.data);
            }
        };


        this.ws.onerror = () => {
            this.updateStatus();
        };

        this.ws.onclose = () => {
            this.updateStatus();
            this.tryReconnect();
        };

        this.updateStatus();
    }

    send(message) {
        const json = typeof message === 'string' ? message : JSON.stringify(message);
        if (this.status === 'connected') {
            this.ws.send(json);
        } else {
            this.messageQueue.push(json);
        }
    }

    tryReconnect() {
        if (!this.retryEnabled || this.status === 'connected' || this.countingDown) return;

        let remaining = this.retryDelay / 1000;
        this.countingDown = true;

        this.onStatusChange('red', `🔴 Disconnected... retrying in ${remaining}s`);

        this.countdownTimer = setInterval(() => {
            remaining--;
            if (!this.retryEnabled) {
                clearInterval(this.countdownTimer);
                this.countingDown = false;
                return;
            }

            if (remaining > 0) {
                this.onStatusChange('red', `🔴 Disconnected... retrying in ${remaining}s`);
            } else {
                clearInterval(this.countdownTimer);
                this.countingDown = false;

                this.reconnectTimer = setTimeout(() => {
                    if (this.retryEnabled && this.status === 'disconnected') {
                        this.connect();
                    }
                }, 0);
            }
        }, 1000);
    }

    toggleRetry() {
        this.retryEnabled = !this.retryEnabled;
        this.clearTimers();

        if (!this.retryEnabled) {
            this.countingDown = false;
            this.onStatusChange('red', '🔴 Retry paused');
        } else {
            this.updateStatus();
        }
    }

    clearTimers() {
        clearTimeout(this.reconnectTimer);
        clearInterval(this.countdownTimer);
        this.reconnectTimer = null;
        this.countdownTimer = null;
        this.countingDown = false;
    }

    updateStatus() {
        switch (this.status) {
            case 'connected':
                this.onStatusChange('green', '🟢 Connected (Real-Time)');
                break;
            case 'connecting':
                this.onStatusChange('yellow', '🟡 Connecting...');
                break;
            case 'closing':
                this.onStatusChange('yellow', '🟡 Closing connection...');
                break;
            case 'disconnected':
                if (this.retryEnabled) this.tryReconnect();
                else this.onStatusChange('red', '🔴 Disconnected (retry paused)');
                break;
            default:
                this.onStatusChange('red', '❓ Unknown state');
        }
    }
}
