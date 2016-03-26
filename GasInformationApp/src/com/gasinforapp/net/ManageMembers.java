package com.gasinforapp.net;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gasinforapp.config.MyConfig;
import com.gasinforapp.config.VolleyUtil;

/**
 * add new member into group or remove useless member
 * 
 * @author zm
 * 
 */
public class ManageMembers {
	/**
	 * 操作单人，需区别是添加还是删除
	 * @param account
	 * @param token
	 * @param groupid
	 * @param memberid
	 * @param memAccount
	 * @param mtype
	 * @param successCallback
	 * @param failCallback
	 */
	public ManageMembers(final String account, final String token,
			final int groupid, final int memberid, final String memAccount,
			final int mtype, final SuccessCallback successCallback,
			final FailCallback failCallback) {
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				MyConfig.SERVER_URL_GROUP + MyConfig.ACTION_MANAGEMEMBERS,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						System.out.println(response);
						try {
							JSONObject obj = new JSONObject(response);

							switch (obj.getInt(MyConfig.KEY_STATUS)) {
							case MyConfig.RESULT_STATUS_SUCCESS:
								if (successCallback != null) {
									successCallback.onSuccess();
								}
								break;
							case MyConfig.RESULT_STATUS_INVALID_TOKEN:
								if (failCallback != null) {
									failCallback
											.onFail(MyConfig.RESULT_STATUS_INVALID_TOKEN);
								}
								break;
							case MyConfig.RESULT_STATUS_NOTFOUND:
								if (failCallback != null) {
									failCallback
											.onFail(MyConfig.RESULT_STATUS_NOTFOUND);
								}
								break;
							case MyConfig.RESULT_STATUS_REPEATED:
								if (failCallback != null) {
									failCallback
											.onFail(MyConfig.RESULT_STATUS_REPEATED);
								}
								break;
							default:
								if (failCallback != null) {
									failCallback
											.onFail(MyConfig.RESULT_STATUS_FAIL);
									break;
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Log.e("errortag", "jsonManagemember");
							if (failCallback != null) {
								failCallback
										.onFail(MyConfig.RESULT_STATUS_FAIL);
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("ManagememberError", error.getMessage(), error);
						if (failCallback != null) {
							failCallback.onFail(MyConfig.RESULT_STATUS_FAIL);
						}
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put(MyConfig.KEY_USER_ACCOUNT, account);
				map.put(MyConfig.KEY_TOKEN, token);
				map.put(MyConfig.KEY_GROUPID, groupid + "");
				map.put(MyConfig.KEY_MEMBERSID, memberid + "");
				map.put(MyConfig.KEY_MEMBERS_MTYPE, mtype + "");
				map.put(MyConfig.KEY_MEMBERSACCOUNT, memAccount);
				return map;
			}
		};
		System.out.println(stringRequest.getUrl());
		stringRequest.setTag("ManageMembers");
		VolleyUtil.getRequestQueue().add(stringRequest);
	}
	/**
	 * 添加多人
	 * @param account
	 * @param token
	 * @param groupid
	 * @param memberids
	 * @param successCallback
	 * @param failCallback
	 */
	public ManageMembers(final String account, final String token,
			final int groupid, final String memberids,
			 final SuccessCallback successCallback,
			final FailCallback failCallback) {
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				MyConfig.SERVER_URL_GROUP + MyConfig.ACTION_ADDMEMBERS,
				new Response.Listener<String>() {
			
			@Override
			public void onResponse(String response) {
				System.out.println(response);
				try {
					JSONObject obj = new JSONObject(response);
					
					switch (obj.getInt(MyConfig.KEY_STATUS)) {
					case MyConfig.RESULT_STATUS_SUCCESS:
						if (successCallback != null) {
							successCallback.onSuccess();
						}
						break;
					case MyConfig.RESULT_STATUS_INVALID_TOKEN:
						if (failCallback != null) {
							failCallback
							.onFail(MyConfig.RESULT_STATUS_INVALID_TOKEN);
						}
						break;
					case MyConfig.RESULT_STATUS_NOTFOUND:
						if (failCallback != null) {
							failCallback
							.onFail(MyConfig.RESULT_STATUS_NOTFOUND);
						}
						break;
					case MyConfig.RESULT_STATUS_REPEATED:
						if (failCallback != null) {
							failCallback
							.onFail(MyConfig.RESULT_STATUS_REPEATED);
						}
						break;
					default:
						if (failCallback != null) {
							failCallback
							.onFail(MyConfig.RESULT_STATUS_FAIL);
							break;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Log.e("errortag", "jsonManagemember");
					if (failCallback != null) {
						failCallback
						.onFail(MyConfig.RESULT_STATUS_FAIL);
					}
				}
			}
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("ManagememberError", error.getMessage(), error);
				if (failCallback != null) {
					failCallback.onFail(MyConfig.RESULT_STATUS_FAIL);
				}
			}
		}) {
			
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put(MyConfig.KEY_USER_ACCOUNT, account);
				map.put(MyConfig.KEY_TOKEN, token);
				map.put(MyConfig.KEY_GROUPID, groupid + "");
				map.put(MyConfig.KEY_MEMBERSIDS, memberids);
				return map;
			}
		};
		System.out.println(stringRequest.getUrl());
		stringRequest.setTag("ManageMembers");
		VolleyUtil.getRequestQueue().add(stringRequest);
	}

	public interface SuccessCallback {
		void onSuccess();
	}

	public interface FailCallback {
		void onFail(int errorCode);
	}
}
