package com.gasinforapp.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gasinforapp.bean.Affairs;
import com.gasinforapp.config.MyConfig;
import com.gasinforapp.config.VolleyUtil;

public class BacklogReplyList {
	/**
	 * 连接到后台办公回复列表
	 * 
	 * @Paarm userId 当前用户职工编号
	 * @param token
	 *            安全标识码
	 * @param page
	 *            页码
	 * @param perpage
	 *            每页数量
	 */
	public BacklogReplyList(final int userId, final String token,
			final int page, final int perpage,final SuccessCallback successCallback,
			final FailCallback failCallback) {
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				MyConfig.SERVER_URL_OA + MyConfig.ACTION_CHECKOFFICEREPLYLIST,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {

						System.out.println(response);

						try {
							JSONObject obj = new JSONObject(response);

							switch (obj.optInt(MyConfig.KEY_STATUS)) {
							// 连接成功
							case MyConfig.RESULT_STATUS_SUCCESS:
								if (successCallback != null) {
									// 事项的标题、请求时间、部门是以JSON数组的格式
									List<Affairs> officeReplyItems = new ArrayList<Affairs>();
									JSONArray affairsJsonArray = obj
											.optJSONArray(MyConfig.KEY_OFFICEREPLYITEMS);
									for (int i = 0; i < affairsJsonArray
											.length(); i++) {
										JSONObject newsObject = affairsJsonArray
												.optJSONObject(i);
										String itemId = newsObject
												.optString(MyConfig.KEY_AFFAIRS_ITEMID);
										String requesterId = newsObject
												.optString(MyConfig.KEY_AFFAIRS_REQUESTER);
										String approverId = newsObject
												.optString(MyConfig.KEY_AFFAIRS_APPROVERID);
										String title = newsObject
												.optString(MyConfig.KEY_AFFAIRS_TITLE);
										String department = newsObject
												.optString(MyConfig.KEY_AFFAIRS_DEPARTMENT);
										String requestTime = newsObject
												.optString(MyConfig.KEY_AFFAIRS_REQUESTTIME);
										String responseTime = newsObject
												.optString(MyConfig.KEY_AFFAIRS_RESPONSETIME);
										String requester = newsObject
												.optString(MyConfig.KEY_AFFAIRS_REQUESTER);
										String approver = newsObject
												.optString(MyConfig.KEY_AFFAIRS_APPROVER);
										int status = newsObject
												.optInt(MyConfig.KEY_AFFAIRS_STATUS);
										String isRead = newsObject
												.optString(MyConfig.KEY_AFFAIRS_READ);

										Affairs as = new Affairs();
										as.setItemId(itemId);
										as.setRequesterId(requesterId);
										as.setApproverId(approverId);
										as.setRequestTitle(title);
										as.setDepartment(department);
										as.setRequestTime(requestTime);
										as.setResponseTime(responseTime);
										as.setApprover(approver);
										as.setRequester(requester);
										as.setOpinion(status);
										as.setIsRead(isRead);
										officeReplyItems.add(as);
									}

									successCallback.onSuccess(page, perpage,officeReplyItems);
								}
								break;
							case MyConfig.RESULT_STATUS_INVALID_TOKEN:
								if (failCallback != null) {
									failCallback
											.onFail(MyConfig.RESULT_STATUS_INVALID_TOKEN);
								}
								break;
							default:
								if (failCallback != null) {
									failCallback
											.onFail(MyConfig.RESULT_STATUS_FAIL);
								}
								break;
							}

						} catch (JSONException e) {
							e.printStackTrace();
							if (failCallback != null) {
								failCallback
										.onFail(MyConfig.RESULT_STATUS_FAIL);
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("TAG", error.getMessage(), error);
						if (failCallback != null)
							failCallback.onFail(MyConfig.RESULT_STATUS_FAIL);
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// 在这里设置需要的参数
				Map<String, String> map = new HashMap<String, String>();
				map.put(MyConfig.KEY_USERID, userId + "");
				map.put(MyConfig.KEY_TOKEN, token + "");
				map.put(MyConfig.KEY_PAGE, page + "");
				map.put(MyConfig.KEY_PERPAGE, perpage + "");
				return map;
			}
		};

		System.out.println(stringRequest.getUrl());

		stringRequest.setTag("optReplyAffairsListpost");
		VolleyUtil.getRequestQueue().add(stringRequest);
	}

	public static interface SuccessCallback {
		void onSuccess(int page, int perpage, List<Affairs> officeReplyItems);
	}

	public static interface FailCallback {
		void onFail(int errorCode);
	}
}