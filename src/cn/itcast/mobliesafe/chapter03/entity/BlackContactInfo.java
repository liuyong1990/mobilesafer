package cn.itcast.mobliesafe.chapter03.entity;

public class BlackContactInfo {
	/**����������*/
	public String phoneNumber;
	/**��������ϵ������*/
	public String contactName;
	/**����������ģʽ</br>   1Ϊ�绰����   2Ϊ��������  3Ϊ�绰�����Ŷ�����*/
	public int mode;
	
	public String getModeString(int mode){
		switch (mode) {
		case 1:
			return "�绰����";
		case 2:
			
			return "��������";
		case 3:
			
			return "�绰����������";

		}
		return "";
	}
}
