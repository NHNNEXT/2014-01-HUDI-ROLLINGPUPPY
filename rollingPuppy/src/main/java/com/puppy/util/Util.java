package com.puppy.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.puppy.dao.impl.ChatInfoDaoImpl;
import com.puppy.dto.ChatInfo;
import com.puppy.dto.ChatRoom;
import com.puppy.dto.Member;

public class Util {
	
	private static final Logger logger = LoggerFactory.getLogger(Util.class);
	
	/*
	 * zoomLevel별 어느거리까지의 마커로 합칠것인지를 위해 존재하는 함수
	 * TODO 알맞은 거리 리서치 (위,경도)
	 */
	public static Float getLatitudeRangeFromZoomLevel(int zoom_level) {
		
		float returnValue = 0;
		if ( zoom_level == 1 ) {
			
			//TODO 알맞은 데이터 리서치
		} else if ( zoom_level == 2 ) { 
			//TODO 알맞은 데이터 리서치
		} else if ( zoom_level == 3 ) {
			//TODO 알맞은 데이터 리서치
		}
		
		return returnValue;
	}

	/*
	 * zoomLevel별 어느거리까지의 마커로 합칠것인지를 위해 존재하는 함수
	 * TODO 알맞은 거리 리서치 (위,경도)
	 */
	public static float getLongitudeRangeFromZoomLevel(int zoom_level) {

		float returnValue = 0;
		
		if ( zoom_level == 1 ) {
			//TODO 알맞은 데이터 리서치
		} else if ( zoom_level == 2 ) { 
			//TODO 알맞은 데이터 리서치
		} else if ( zoom_level == 3 ) {
			//TODO 알맞은 데이터 리서치
		}
		
		return returnValue;
	}

	/*파라미터인 chatRoom Lists는 tbl_marker_id로 정렬되어있다.
	 * 이 메서드는 chatRoom객체를 중심으로 리턴되는 JSON데이터를
	 * Marker중심으로 변환하기 위하여 사용한다.
	 * 즉 예를들어서 리턴되는 Json데이터가 아래와 같을 경우 Javascript에서 매번 Marker값을 구하기위해 계산을 여러번 해야한다.
	 * 
	 * [
	 *  	{ id : "122", location_name: "NHN NEXT1" ....  , tbl_marker_id: "4"}   		//chatRoom의 첫번째 객체에 해당하는 데이터
	 * 		{ id : "123", location_name: "NHN NEXT2" ....  , tbl_marker_id: "5"}    		//chatRoom의 두번째 객체에 해당하는 데이터
	 *      ...
	 *      ...
	 *      ...
	 * ]
	 * 
	 * 왜 여러번 계산해야 하나? 
	 * 맵상에 존재하는 마커를 식별하는 유일한 값은 "마커아이디"값인데, return하는 JSON값이 위와 같은 형태일 경우, 채팅방이 기준으로 sort되어 있기때문이다.
	 * 
	 * 그래서 return data를 아래와 같은 형태로 만들기 위해 이 함수를 만들었다. 
	 * 
	 * {
	 *  	id: "5",  																										//마커를 유일하게 식별하는 아이디값
	 *  	location_name: "NHN NEXT2", 
	 *  	location_latitude: "37.49962920", 
	 *  	location_longitude: "127.03207780", 
	 *  	chatRooms: [.....] 																						//채팅방에 해당하는 chatRoom객체 리스트
	 * } 
	 */
	public static List<JsonMarker> getMarkerCentralListFromChatRoomList(List<ChatRoom> lists) {
		
		if ( lists == null || lists.size() == 0 )
			return null;
		
		//반환할 리스트를 담을 그릇
		List<JsonMarker> returnList = new ArrayList<JsonMarker>();
		
		//현재의 타겟마커를 가리키는 변수선언
		JsonMarker currentMarker = null;
		
		for ( int index = 0 ; index < lists.size() ; ++index ) {
			
			//index에 해당하는 ChatRoom 객체를 변수에 할당 (여러번 불러와서 사용할 거니까, 매번 lists.get(index)하지 않도록)
			ChatRoom room = lists.get(index);
			
			//만약 currentMarker가 null일경우 (맨 처음 실행될때를 의미)
			//currentMarker의 아이디값과 room이 할당되어 있는 marker_id가 다를경우, 새로운 Marker객체를 생성한다.
			if ( currentMarker == null || currentMarker.getId() != room.getTblMarkerId() ) {
				currentMarker = new JsonMarker(room.getTblMarkerId());
				currentMarker.setLocation_latitude(room.getLocationLatitude());
				currentMarker.setLocation_longitude(room.getLocationLongitude());
				currentMarker.setLocation_name(room.getLocationName());
				returnList.add(currentMarker);
			}
			//currentMarker가 room채팅방이 속해야 하는  Marker객체가 맞으므로, chatRoom객체의 데이터를 그대로 더해준다.
			currentMarker.addChatRooms(
					new JsonChatRoom( room.getId(), room.getTitle(), room.getMax() ));
		}

		return returnList;
	}

	
	/*
	 * 다음과 같은 데이터형태를 만들기 위한 함수이다.
	 * 
	 * //oInfo는 다음과 같은 형태이다.
	 *  {
	 *		"채팅방번호": {
	 *			title: "",
	 *			locationName: "", 
	 *			max: "",
	 *			participantNum: "",
	 *			unreadMessageNum: "", 
	 *			oPaticipant: {
	 *				"회원아이디": 
	 *				{
	 *					nickname: "",
	 *				}
	 *			}
	 *		}
	 *	} 
	 */
	public static Map<String, JsonChatInfo> getChatRoomInfoObjectFromQueryResult(List<ChatInfo> myChatInfoList) {
		/*
		 * Database query를 위한 선언
		 * 채팅방리스트에 참여하고 있는 
		 * 모든 참여자정보를 가져오기 위해 질의가 필요하다.
		 */
		ChatInfoDaoImpl myChatInfoDaoImpl = ChatInfoDaoImpl.getInstance(); 
		
		//전체 참여자아이디가 구분자,를 기준으로 구성된 
		//String Value를 리턴한다.
		//ex) "1,2,3,4,5"...
		String totalListString = Util.getTotalStringList(myChatInfoList);
		
		//totalListString을 통해서 member데이터를 검색해온다.
		List<Member> memberList = myChatInfoDaoImpl.selectAllParticipantData(totalListString);
		
		//쿼리결과로 가져오는 memberList를 손쉽게 활용하기 위해
		//List를 "회원아이디: Member클래스"로 만들어주는 Util클래스를 호출한다.
		//[Member, Member, Member, Member ...] -> { "아이디1": Member, "아이디2": Member, "아이디3": Member, "아이디4": Member...}
		Map<Integer, Member> memberTotalDataFromQuery = Util.getKeyValueMemberListFromQueryResult(memberList); 
		
		//myChatInfo로부터 (key: 채팅방번호, value: 채팅방에 참여하고있는 참여자 아이디리스트 )를 가져온다.
		Map<Integer, List<String>> participantsTotalData = Util.getParticipantListFromChatInfoList(myChatInfoList);
		
		//리턴데이터를 담는 그릇
		Map<String, JsonChatInfo> resultMap = new HashMap<String, JsonChatInfo>(); 
		
		/*
		 * for문을 돌면서 원하는 형태로 데이터를 담는다.
		 * 
		 */
		for (ChatInfo chatRoom : myChatInfoList) {
			//chatRoomId를 가져온다.
			int chatRoomId = chatRoom.getChatRoomId();
			
			//채팅방 번호에 해당하는 참여자리스트를 가져온다.
			List<String> targetParticipantList =  participantsTotalData.get(chatRoomId);
			
			//참여자리스트를 JsonParticipant형태로 만든다.
			//{"회원아이디1" : JsonParticipant, "회원아이디2" : JsonParticipant, "회원아이디3" : JsonParticipant...}
			Map<String, JsonParticipant> insertParticipantData = new HashMap<String, JsonParticipant>();
			
			//방에 참여하고 있는 채팅방참여 리스트를 돌면서 참여자아이디를 하나씩 가져온다.
			for (String targetMemberId : targetParticipantList) {
				//참여자 아이디와 일치하는 데이터베이스 결과데이터 DTO를 가져온다.
				int memberId = Integer.parseInt(targetMemberId.toString());
				
				Member targetMember = memberTotalDataFromQuery.get(memberId);
				
				if ( targetMember == null )
					continue;
				
				//데이터삽입
				JsonParticipant insertData = new JsonParticipant
																		(
																			targetMember.getNicknameAdjective(),
																			targetMember.getNicknameNoun(),
																			Util.getHexBackgroundColorFromSeed(memberId),
																			Util.getBackgroundImageUrlFromSeed(memberId)
																		);
				
				insertParticipantData.put(targetMemberId, insertData);
			}
			
			//리턴데이터에 추가한다.
			//채팅방 번호를 키로 가지는 Map에
			//
			//{"채팅방 아이디" : {"참여자아이디1": JsonParticipant, "참여자아이디2": JsonParticipant ...}로 더한다.
			resultMap.put(
									"" + chatRoomId, 
									new JsonChatInfo(
																	chatRoom.getChatRoomTitle(), 
																	chatRoom.getLocationName(), 
																	chatRoom.getMax(), 
																	targetParticipantList.size(),
																	chatRoom.getUnreadMessageNum(),
																	insertParticipantData
																)
									);
		}
		
		return resultMap;
	}
	
	public static String getBackgroundImageUrlFromSeed(int memberId) {
		int imageNum = (memberId % 15)+1;
		return "/images/userIcons/"+ imageNum  +".png";
	}

	public static String getHexBackgroundColorFromSeed(int seed) {
		return "#"+Integer.toHexString((int) Math.floor((Math.abs(Math.sin(seed) * 16777215)) % 16777215));
	}

	/*
	 * Camel Convention으로 이루어진 StringData를
	 * UnderBar Convention의 String으로 변경한다.
	 * 
	 * ex) tblChatRoom -> tbl_chat_room;
	 */
	public static String getUnderBarlConventionString(String carmelValue) {
		
		StringBuilder sb = new StringBuilder();
		
		for ( int index = 0 ; index < carmelValue.length() ; ++ index ) {
			char target = carmelValue.charAt(index);
			
			if (isUpperCase(target)) {
				sb.append("_");
				sb.append(new Character(target).toString().toLowerCase());
			} else {
				sb.append(target);
			}
		}

		return sb.toString();
	}
	
	/*
	 * 대문자 여부를 리턴
	 */
	private static boolean isUpperCase(char ch) {
	    return ch >= 'A' && ch <= 'Z';
	}

	/*
	 * MyChatInfo DTO를 참고하자.
	 * MyChatInfo데이터로부터 아래와같이 생긴 MapData를 리턴한다.
	 * 
	 * {"채팅방번호1": [참여자1, 참여자2, 참여자3, 참여자4 ...], "채팅방번호2": [참여자1, 참여자2, 참여자3, 참여자4 ...] ...} 
	 */
	public static Map<Integer, List<String>> getParticipantListFromChatInfoList(List<ChatInfo> chatInfoList) {
		Map<Integer, List<String>> resultMap = new HashMap<Integer, List<String>>();

		for (ChatInfo myChatInfo : chatInfoList) {
			resultMap.put(myChatInfo.getChatRoomId(), getListFromString(myChatInfo.getParticipantList()));
		}
		
		return resultMap;
	}

	/*
	 * ,를 기준으로 구분된 단어들의 조합을
	 * List형태로 반환해준다.
	 * 
	 * ex) "a,b,c" -> ["a","b","c"]
	 */
	private static List<String> getListFromString(String participantList) {
		return new ArrayList<String>(Arrays.asList(participantList.split(",")));
	}

	/*
	 * ,를 기준으로 구분된 단어들의 조합을 모두합쳐서
	 * 하나의 긴 단어조합을 만들어준다. (마지막,는 substring으로 삭제한다)
	 * 
	 * ex) ["a,b,c", "1,2,3", "가,나,다"] -> "a,b,c,1,2,3,가,나,다"
	 */
	public static String getTotalStringList(List<ChatInfo> chatInfoList) {
		
		String returnString = "";
		
		for (ChatInfo myChatInfo : chatInfoList) {
			returnString += myChatInfo.getParticipantList() + ",";
		}
		
		if ( returnString.length() !=0 ) {
			returnString =returnString.substring(0, returnString.length()-1);
		}
		
		logger.info("resultString : "+returnString);
		return returnString;
	}

	/*
	 * MemberList를 아이디를 기준으로하는 Key, Value데이터로 변환한다.
	 * 
	 * ex) [Member, Member, Member, Member] -> {"아이디1": Member, "아이디2": Member, "아이디3": "Member"...}
	 */
	public static Map<Integer, Member> getKeyValueMemberListFromQueryResult(List<Member> memberList) {
		
		Map<Integer, Member> returnData = new HashMap<Integer, Member>();
		
		if ( memberList != null ) {
			for (Member member : memberList) {
				returnData.put(member.getId(), member);
				logger.info("meber id : "+member.getId());
			}
		}
		
		return returnData;
	}

	public static String  getJavscriptStringValueThatAlertMessageAndRedirectParameterURL(String message, String redirectUrl) {
		try {
			return "<script>alert(decodeURI('"+URLEncoder.encode(message,"utf8").replace("+",  "%20")+"'));window.location='"+redirectUrl+"';</script>";
		} catch (UnsupportedEncodingException e) {
			logger.info("getJavascript Fail");
			return "<script>alert('Unexpected Error Occur!! Notice Homepage Administer And Try Again!')</script>";
		}
	}
	
	public static String  getJavscriptStringValueThatAlertMessageAndMovePrevious(String message) {
		try {
			return "<script>alert(decodeURI('"+URLEncoder.encode(message,"utf8").replace("+",  "%20")+"'));history.back(-1);</script>";
		} catch (UnsupportedEncodingException e) {
			logger.info("getJavascript Fail");
			return "<script>alert('Unexpected Error Occur!! Notice Homepage Administer And Try Again!')</script>";
		}
	}
	
	public static String  getJavscriptStringValueThatAlertMessage(String message) {
		try {
			return "<script>alert(decodeURI('"+URLEncoder.encode(message,"utf8").replace("+",  "%20")+"'));</script>";
		} catch (UnsupportedEncodingException e) {
			logger.info("getJavascript Fail");
			return "<script>alert('Unexpected Error Occur!! Notice Homepage Administer And Try Again!')</script>";
		}
	}
}
