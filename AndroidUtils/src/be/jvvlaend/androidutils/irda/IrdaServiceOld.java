package be.jvvlaend.androidutils.irda;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.os.IBinder;

public class IrdaServiceOld extends Service {
	private Object irdaService;
	private Method irdaSend;
	private Method irdaReceive;
	private String errorMessage;
	private ConsumerIrManager consumerIrManager;

	public IrdaServiceOld(Activity activity) {
		try {
			loadService(activity);
		} catch (NoSuchMethodException e) {
			errorMessage = e.getMessage();
		}
	}

	// private void loadService(Activity activity) {
	// consumerIrManager = (ConsumerIrManager)
	// activity.getSystemService(CONSUMER_IR_SERVICE);
	// }

	private void loadService(Activity activity) throws NoSuchMethodException {
		irdaService = activity.getSystemService("irda");
		if (irdaService != null) {
			Class<? extends Object> irdaClass = irdaService.getClass();
			Class params[] = new Class[1];
			params[0] = String.class;
			irdaSend = irdaClass.getMethod("write_irsend", params);
			irdaReceive = irdaClass.getMethod("read_irsend");
		} else {
			errorMessage = "IrdaService is null";
		}
	}

	// public void sendInfraRed(String code) throws InfraRedException {
	// if (consumerIrManager.hasIrEmitter()) {
	// consumerIrManager.transmit(38400, transformCodes(code));
	// }
	// }
	//
	// private int[] transformCodes(String code) {
	// StringTokenizer st = new StringTokenizer(code);
	// int[] intCodes = new int[st.countTokens()];
	// int pos = 0;
	// while (st.hasMoreTokens()) {
	// String token = st.nextToken();
	// }
	// return intCodes;
	// }
	//
	private String hex2dec(String irData) {
		List<String> list = new
				ArrayList<String>(Arrays.asList(irData.split(" ")));
		list.remove(0); // dummy
		int frequency = Integer.parseInt(list.remove(0), 16); // frequency
		list.remove(0); // seq1
		list.remove(0); // seq2

		for (int i = 0; i < list.size(); i++) {
			list.set(i, Integer.toString(Integer.parseInt(list.get(i), 16)));
		}

		frequency = (int) (1000000 / (frequency * 0.241246));
		list.add(0, Integer.toString(frequency));

		irData = "";
		for (String s : list) {
			irData += s + ",";
		}
		return irData;
	}

	public void sendInfraRed(String code) throws InfraRedException {
		try {
			if (irdaService != null) {
				irdaSend.invoke(irdaService, code);
			}
		} catch (IllegalArgumentException e) {
			throw new InfraRedException(e);
		} catch (IllegalAccessException e) {
			throw new InfraRedException(e);
		} catch (InvocationTargetException e) {
			throw new InfraRedException(e);
		}
	}

	public String receiveInfraRed() throws InfraRedException {
		try {
			if (irdaService != null) {
				return irdaReceive.invoke(irdaService, new Object[0]).toString();
			}
			return null;
		} catch (IllegalArgumentException e) {
			throw new InfraRedException(e);
		} catch (IllegalAccessException e) {
			throw new InfraRedException(e);
		} catch (InvocationTargetException e) {
			throw new InfraRedException(e);
		}
	}

	public boolean isServiceAvailable() {
		return irdaService != null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
