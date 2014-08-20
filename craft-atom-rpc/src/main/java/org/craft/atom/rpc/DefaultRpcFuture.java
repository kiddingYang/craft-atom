package org.craft.atom.rpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.craft.atom.protocol.rpc.model.RpcMessage;

/**
 * @author mindwind
 * @version 1.0, Aug 19, 2014
 */
public class DefaultRpcFuture implements RpcFuture {
	
	
	private RpcMessage response ;
	private Throwable  throwable;
	private boolean    ready    ;
	private int        waiters  ;
	
	 
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		long timeoutMillis = unit.toMillis(timeout);
		long endTime       = System.currentTimeMillis() + timeoutMillis;
		synchronized (this) {
			if (ready)              return ready;
			if (timeoutMillis <= 0) return ready;
			waiters++;
			try {
				while (!ready) {
					wait(timeoutMillis);
					if (endTime < System.currentTimeMillis() && !ready) {
						throwable = new TimeoutException();
					}
				}
			} finally {
				waiters--;
			}
		}
		return ready;
	}

	@Override
	public Throwable getThrowable() {
		synchronized (this) {
			return throwable;
		}
	}

	@Override
	public RpcMessage getResponse() throws IOException, TimeoutException {
		Throwable t = getThrowable();
		if (t != null) {
			if (t instanceof IOException     ) throw (IOException)      t;
			if (t instanceof TimeoutException) throw (TimeoutException) t;
			throw new RpcException(RpcException.UNKNOWN, t);
		}
		
		synchronized (this) {
			return response;
		}
	}

	@Override
	public void setThrowable(Throwable throwable) {
		synchronized (this) {
			if (ready) return;
			this.throwable = throwable;
			ready = true;
			if (waiters > 0) {
                notifyAll();
            }
		}
	}

	@Override
	public void setResponse(RpcMessage response) {
		synchronized (this) {
			if (ready) return;
			this.response = response;
			ready = true;
			if (waiters > 0) {
                notifyAll();
            }
		}
	}

}