package jp.ne.ruru.park.ando.diejavaco2;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XML�f�[�^�u����B
 * �f�[�^�֘A�̊Ǘ���S�����܂��B
 * @author ����
 *
 */
public class XMLData {
	
	/** �f�[�^�̓ǂݍ��� */
	public XMLData() {
		DocumentBuilderFactory factory;
		DocumentBuilder        builder;
		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			factory.setIgnoringElementContentWhitespace(true);
			factory.setIgnoringComments(true);
			factory.setValidating(true);
			Node root    = builder.parse("src/main/config/xml/data.xml");
			Set<String> sameNameChek = new TreeSet<String>();
			invokeLoop(root,null,sameNameChek);
		} catch (ParserConfigurationException e0) {
			System.out.println(e0.getMessage());
		} catch (SAXException e1){
			System.out.println(e1.getMessage());
		} catch (IOException e2) {
			System.out.println(e2.getMessage());
		}
	}
	/**
	 * ���[�v���� 
	 * @param parentNode �e�m�[�h
	 * @param parentEvent �e�C�x���g
	 * @param sameNameChek �����`�F�b�N�p
	 */
	protected void invokeLoop(Node parentNode,Event parentEvent,Set<String> sameNameChek) {
		final NodeList nodes = parentNode.getChildNodes();
		if (nodes == null) {
			return;
		}
		IntStream
			.range(0,nodes.getLength())
			.mapToObj(nodes::item)
			.filter(n->n instanceof Element)
			.map(n->(Element)n)
			.forEach(e->{
				switch (e.getTagName()) {
				case "root": {
					this.invokeLoop(e, parentEvent,sameNameChek);
					break;
				}
				case "sinario":{
					EventSinario event = new EventSinario(e,parentEvent,sameNameChek);
					getSinarioList().add(event);
					this.invokeLoop(e, event,sameNameChek);
					break;
				}
				case "contry":{
					EventContry event =
							new EventContry(e,sameNameChek,getContryList().size());
					getContryList().add(event);
					this.invokeLoop(e, event,sameNameChek);
					break;
				}
				case "person":{
					EventPerson event = new EventPerson(e,sameNameChek);
					// �e��ڑ�����
					((EventContry)parentEvent).getPersonList().add(event);
					event.setContry((EventContry)parentEvent);
					this.invokeLoop(e, event,sameNameChek);
					break;
				}
				case "message": {
					String target = e.getTextContent();
					Arrays.asList(target.split("[\r\n]+"))
					.stream()
					.forEachOrdered(
							message->{
								String ans = message.replaceFirst("^\\s+","");
								if ((ans != null) && (!ans.equals(""))) {
									parentEvent.addMessage(ans);
								}
							});
					break;
				}
				case "location":{
					EventLocation event = new EventLocation(e,sameNameChek);
					this.getLocationSet().add(event);
					this.invokeLoop(e, event,sameNameChek);
					break;
				}
				case "if": {
					break;
				}
				case "selection": {
					String name = e.getAttribute("name");
					if (name == null) {
						name = "";
					}
					//
					String value = e.getAttribute("value");
					int valueInt;
					if ((value == null) || value.equals("")) {
						valueInt = 0;
					} else {
						try {
							valueInt = Integer.parseInt(value);
						} catch(NumberFormatException e2) {
						valueInt = 0;
						}
					}
					if (parentEvent instanceof EventContry) {
						for (int i = 0 ; i < SelectionData.DATA.length ; i++ ) {
							String signature = SelectionData.DATA[i].signature.name;
							if (signature.equals(name)) {
								((EventContry)parentEvent).selectRate[i] = valueInt;
								break;
							}
						}
					} else if (parentEvent instanceof EventSinario) {
						if (((EventSinario)parentEvent).selectRate == null) {
							((EventSinario)parentEvent).selectRate = new int[SelectionData.DATA.length];
						}
						for (int i = 0 ; i < SelectionData.DATA.length ; i++ ) {
							String signature = SelectionData.DATA[i].signature.name;
							if (signature.equals(name)) {
								((EventSinario)parentEvent).selectRate[i] = valueInt;
								break;
							}
						}
					} else {
						System.out.println("sinario error parent=" + parentEvent.toString());
					}
					break;
				}
				default:
					System.out.println("unkown tag tag=" + e.getTagName());
				}
				//this.invokeLoop(parentNode, parentEvent);
			});
	}
	
	/**
	 * ���P�[�V�����̎擾 
	 * @return ���P�[�V����
	 */
	public Set<EventLocation> getLocationSet() {
		return locationList;
	}
	/**
	 * �V�i���I�̎擾 
	 * @return �V�i���I
	 */
	public List<EventSinario> getSinarioList() {
		return sinaroList;
	}
	/**
	 * ���P�[�V�������珊�������擾���� 
	 * @param location ���P�[�V����
	 * @return ������
	 */
	public EventContry getLocationToContry(EventLocation location) {
		// �x�z������������
		EventContry targetConttry = null;
		for (EventContry contry : this.getContryList()) {
			if (contry.getLocationSet().stream()
					.anyMatch(loc-> loc== location)) {
				targetConttry = contry;
				break;
			}
		}
		return targetConttry;
	}
	
	/** ���z�G���̎擾
	 * @param myContry
	 * @return �J���g���[�ԍ�
	 */
	protected EventContry getEnemyContry(EventContry myContry) {
		EventContry targetContryNum = null;
		int like = Integer.MAX_VALUE;
		for (EventContry nowContry: this.getContryList()) {
			int i = nowContry.getContryNumber();
			if (nowContry == myContry) {
				continue; // ����������
			}
			if (!nowContry.isAlive()) {
				continue; // ���̍��͎���ł���
			}
			if (myContry.getLike()[i] < like) {
				targetContryNum = nowContry;
				like =  myContry.getLike()[i];
			}
		}
		return targetContryNum;
	}
	/**
	 * �ړ���̌��� 
	 * @param person �l���
	 */
	public void invokeMovePersion(EventPerson person) {
		// ���n��̃��P�[�V�������擾
		if (person.getLocation() == null) {
			person.setToLocation(null); // �ړ����Ȃ�
			return; // �ړ����Ȃ��Ƃ������ł��Ȃ�
		}

		//
		// �ړ��\�ȃ��P�[�V�����̃��X�g
		Set<EventLocation> ansLocationList;
		switch (person.getSelection().move) {
		case 1: // �����_��
			// �����_���Ȑl�͌��Ƃ��Ă��ׂĂ�������
			ansLocationList = person.getLocation().getLocationSet();
			break;
		case 2: // �h�q
			ansLocationList = findNearEnemyContry(person);
			//
			break;
		case 3: // �퓬
			ansLocationList = findNearEnemyContry(person);
			break;
		case 4: // ������
			// ������l�p
			// �ړ��\�ȃ��P�[�V�����ɓG�����Ȃ��ꏊ�ֈړ�
			ansLocationList =
					person.getLocation().getLocationSet().stream()
					.filter(
							nowLocation->
							! this.getContryList().stream()
							.filter(contry->contry != person.getContry())
							.flatMap(contry->contry.getArivePersonList().stream())
							.anyMatch(pson->pson.getLocation() == nowLocation))
					.collect(Collectors.toSet());
			if (ansLocationList.isEmpty()) {
				//�l�ʑ^�̂Ȃ烉���_���ɓ����ċʍӂ���
				ansLocationList = person.getLocation().getLocationSet();
			}
			break;
		default:
			// �ړ����Ȃ�
			ansLocationList = new TreeSet<EventLocation>();
		}		
		// �ړ��惊�X�g����ړ��������
		if (ansLocationList.isEmpty()) {
			person.setToLocation(null); // �ړ����Ȃ�
			return;
		} 
		Random rand = new Random();
		EventLocation location =
				ansLocationList.stream()
				.collect(Collectors.toList())
				.get(rand.nextInt(ansLocationList.size()));
		person.setToLocation(location);
	}

	/**
	 * ��ԋ߂����P�[�V������Ԃ� 
	 * @param person �l���
	 * @return ���ʂ̃��X�g
	 */
	public Set<EventLocation> findNearEnemyContry(EventPerson person) {
		Set<EventLocation> targetLocationList = new TreeSet<EventLocation>();
		FildResult result = null;
		//
		//���ݒn�_�����Ȃ炤�������Ⴉ��
		result = this.getLocationType(-1,person.getContry(),person.getLocation());
		if (result != null) {
			return targetLocationList;
		}
		//
		//System.out.println("------------------------");
		for (EventLocation targetLocation : person.getLocation().getLocationSet()) {
			if (person.getSelection().move == 2) {
				if (!person.getContry().getLocationSet().contains(targetLocation)) {
					// �ړ����[�h�Ȃ玩�����ȊO�͑I���}����O�����A
					// �u�����_�œ����Ȃ��v���m�肷��
					return new TreeSet<EventLocation>();
				}
			}
			//System.out.println("�n��=" + targetLocation);
			FildResult nowResult = this.findNearEnemyContry(person.getContry(),targetLocation);
			//if (nowResult != null) {
			//	nowResult.display();
			//}
			if (result == null) {
				result = nowResult;
				targetLocationList.add(targetLocation);
			} else {
				int index = result.compareTo(nowResult);
				if (index == 0) {
					targetLocationList.add(targetLocation);
				} else if (0 < index) {
					targetLocationList.clear();
					targetLocationList.add(targetLocation);
					result = nowResult;
				}
			}
		}
		return targetLocationList;
	}
	/** ���ʂƂ��ē�����ړ���D��x */
	protected class FildResult {
		/**
		 * �R���X�g���N�^
		 * @param level ���x��
		 * @param myContryFlag �����Ȃ�true
		 * @param notEnemyContryFlag �G��������Ȃ�true/�������œG�����Ă�true
		 * @param like �אڍ��̗F�D�x
		 * @param hp ��̓GHP���v-����HP���v(���Ȃ����U�߂₷��)
		 */
		public FildResult(int level,boolean myContryFlag,boolean notEnemyContryFlag,int like,int hp) {
			this.level = level;
			this.myContryFlag = myContryFlag;
			this.notEnemyContryFlag = notEnemyContryFlag;
			this.like = like;
			this.hp = hp;
		}
		/**
		 * �ʒu���̗D������߂�]���֐�
		 * @param o �E���̒l
		 * @return �E�����D��Ȃ琳�̐��l�ɂȂ�
		 */
		public int compareTo(FildResult o) {
			if (o == null) {
				return -1; // �E����null�Ȃ̂ō��������I�ׂȂ�
			}
			if (this.level > o.level) {
				return 1; // �T�����x���͂������������D�悳���
			}
			if (this.level < o.level) {
				return -1;
			}
			if (this.myContryFlag && o.myContryFlag) {
				// ���������������ł���΁A�G���������傫������
				if (this.notEnemyContryFlag && o.notEnemyContryFlag) {
					// ���������������œG������
					return 0;
				} else if (!this.notEnemyContryFlag && o.notEnemyContryFlag) {
					return 1; // �E���ɓG������
				} else if (this.notEnemyContryFlag && !o.notEnemyContryFlag) {
					return -1; // �����ɓG������
				}
				// �����ɓG������
				return 0;
			} else if (this.myContryFlag && !o.myContryFlag) {
				// �E���������Ȃ�
				if (!this.notEnemyContryFlag) {
					return -1; // �����ɓG������
				}
				return 1; //
			} else if (!this.myContryFlag && o.myContryFlag) {
				// �����������Ȃ�
				if (!o.notEnemyContryFlag) {
					return 1; // �����ɓG������
				}
				return -1;
			}
			// �����Ƃ������łȂ��Ȃ�
			if (this.notEnemyContryFlag && o.notEnemyContryFlag) {
				// �����Ƃ��ɓG���łȂ�(�󂢂Ă���)
				return 0;
			} else if (this.notEnemyContryFlag && !o.notEnemyContryFlag) {
				return -1; // �E�ɓG��������
			} else if (!this.notEnemyContryFlag && o.notEnemyContryFlag) {
				return 1; // ���ɓG��������
			}
			// �����Ƃ��G���Ȃ�
			if (this.like < o.like) {
				return -1; // �����̕��������Ă���
			}
			if (this.like > o.like) {
				return 1; // �E���̕��������Ă���
			}
			// �����Ƃ��F�D�l�������Ȃ�
			if (this.hp < o.hp) {
				return -1; // �����̕���HP�����Ȃ�
			}
			if (this.hp > o.hp) {
				return 1; // �E���̕���HP�����Ȃ�
			}
			return 0; 
		}
		/** ��Ԃ�\��
		 * �f�o�b�O�p�ł�
		 */
		public void display() {
			System.out.println("  lev=" + level + " �����H=" + myContryFlag + " not�G���H=" + notEnemyContryFlag + " �D���H=" + like);
		}
		
		/** �T���K�w */
		public final int level;

		/** ������� */
		public final boolean myContryFlag;
		
		/** �G���łȂ��H */
		public final boolean notEnemyContryFlag;
		
		/** �F�D�l */
		public final int like;
		
		/** ��̓GHP���v-����HP���v(���Ȃ����U�߂₷��) */
		public final int hp;
	}
	
	/** �T���K�w�ƒn������܂Ƃ߂��N���X */
	public class LevelLocation {
		/**
		 * �R���X�g���N�^
		 * @param level �T���K�w
		 * @param location �n����
		 */
		public LevelLocation(int level,EventLocation location) {
			this.level = level;
			this.location = location;
		}
		/** �T���K�w */
		public final int level;
		
		/** �ʒu */
		public final EventLocation location;
	}
	
	/**
	 * �ł��߂��אڒn�����^�T���ŒT��
	 * @param contry �]���Ώۂ̎���
	 * @param loc �]���Ώےn
	 * @return �אڒn�̕]���l
	 */
	public FildResult findNearEnemyContry(EventContry contry,EventLocation loc) {
		Set<EventLocation> oldLocation = new TreeSet<EventLocation>();
		Set<EventLocation> targetLocationList = new TreeSet<EventLocation>();
		Deque<LevelLocation> queue = new ArrayDeque<LevelLocation>();
		int level = 0;
		queue.offer(new LevelLocation(level,loc));
		FildResult result = null;
		while (!queue.isEmpty()) {
			LevelLocation levelLocation = queue.poll();
			if (oldLocation.contains(levelLocation.location)) {
				//�Â��͖̂������Ă悢
				continue;
			}
			oldLocation.add(levelLocation.location);
			targetLocationList.add(levelLocation.location);
			final int nextLevel = level + 1;
			levelLocation.location.getLocationSet().stream().forEachOrdered(
				location->queue.offer(new LevelLocation(nextLevel,location)));
			if (queue.isEmpty() || (queue.peek().level != level)) {
				// ���^�����łP���x�����̍Ō�ɂ��ǂ蒅��������A�܂Ƃ߂ĂP�{�������������Ōv�Z
				//
				for (EventLocation targetLoation : targetLocationList) {
					FildResult nowResult = getLocationType(level,contry,targetLoation);
					if (result == null) {
						result = nowResult;
					} else if ((nowResult != null) && (0 < result.compareTo(nowResult))) {
						result = nowResult;
					}
				}
				if (result != null) {
					break;
				}
				//
				// ���̒T���̂��߂ɏ���
				targetLocationList.clear();
				// ���̃��x����ݒ�
				level++;
			}
		}
		return result;
	}	
	
	/**
	 * ���̒n��̗F�D�����擾
	 * @param level �T���K�w
	 * @param contry �G��
	 * @param targetLoctaion �n��
	 * @return �F�D���
	 */
	public FildResult getLocationType(int level,EventContry contry,EventLocation targetLoctaion) {
		boolean myContryFlag;
		boolean notEnemyContryFlag;
		int disLike = Integer.MAX_VALUE;
		int hp = 0;
		if (contry.getLocationSet().contains(targetLoctaion)) {
			myContryFlag = true;
			notEnemyContryFlag = 
					! this.getContryList().stream()
					.filter(cont->cont != contry)
					.flatMap(cont->cont.getArivePersonList().stream())
					.anyMatch(p->p.getLocation() == targetLoctaion);
			if (notEnemyContryFlag) {
				// �����G�����Ȃ��̂Ȃ�
				return null;
			}
		} else {
			myContryFlag = false;
			EventContry enemyContry = null;
			for (EventContry cont : this.getContryList()) {
				if (cont == contry) {
					continue;
				}
				if (cont.getLocationSet().contains(targetLoctaion)) {
					enemyContry = cont;
					break;
				}
			}
			if (enemyContry == null) {
				notEnemyContryFlag = true;
			} else {
				notEnemyContryFlag = false;
				disLike = contry.getLike()[enemyContry.getContryNumber()];
			}
			
			// �G���̂��̏��HP����A������HP�����������ʂ𓾂�
			hp  = 
				 this.getContryList().stream()
				 .filter(cont->cont != contry) // �G���v���X
				 .flatMap(cont->cont.getArivePersonList().stream())
				 .filter(person->person.getLocation() == targetLoctaion)
				 .mapToInt(person->person.getHp()).sum()
				 - this.getContryList().stream()
				 .filter(cont->cont == contry) // �������}�C�i�X
				 .flatMap(cont->cont.getArivePersonList().stream())
				 .filter(person->person.getLocation() == targetLoctaion)
				 .mapToInt(person->person.getHp()).sum();
		}
		return new FildResult(level,myContryFlag,notEnemyContryFlag,disLike,hp);
	}
	
    /**
     * �S���ƈꗗ
     * @return �S���Ƃ̃��X�g
     */
    public List<EventContry> getContryList() {
    	return this.contryList;
    }
	
	/** �}�b�v�f�[�^ */
	private Set<EventLocation> locationList = new TreeSet<EventLocation>();
	
	/** ���f�[�^ */
	private List<EventContry> contryList = new ArrayList<EventContry>();

	/** �V�i���I�f�[�^ */
	private List<EventSinario> sinaroList = new ArrayList<EventSinario>();

}
