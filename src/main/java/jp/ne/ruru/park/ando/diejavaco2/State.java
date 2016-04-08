package jp.ne.ruru.park.ando.diejavaco2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import jp.ne.ruru.park.ando.diejavaco2.swt.FrameInvokMediator;
import jp.ne.ruru.park.ando.diejavaco2.swt.ImageFactory;

/** �����Ԃ��Ď�����N���X
 * ������̃v���O�����{�̂ł��B
 * ��Ԋ֘A�̊Ǘ���S�����܂��B
 * @author ����
 *
 */
public class State {
	
	/** ���݂̏�� */
	public enum NOW {
		
		/** �n�܂�O���� */
		START_PRE(    "START_PRE"),
		
		/** �n�܂鏈�� */
		START(        "START"),
		
		/** �n�܂鏈���̌㏈�� */
		START_POST(   "START_POST"),
		
		/** �}�b�v�����̑O���� */
		MAP_PRE(   "MAP_PRE"),
		
		/** �}�b�v�����̌㏈�� */
		MAP_CREATE("MAP_CREATE"),
		
		/** �n�k�ϓ� */
		MAP_MOVE(  "MAP_MOVE"),
		
		/** �}�b�v�����̌㏈�� */
		MAP_POST(  "MAP_POST"),

		/** ���t�F�[�Y�̑O���� */
		SELECT_PRE(     "SELECT_PRE"),
		
		/** 1���P�ʏ����̑O���� */
		SELECT_CONT_PRE("SELECT_CONT_PRE"),
		
		/** 1�Z���P�ʂ̏��� */
		SELECT_CONT(    "SELECT_CONT"),
		
		/** ���[�U�����̏��� */
		SELECT_CONT_WAIT_PRE( "SELECT_CONT_WAIT_PRE"),
		
		/** ���[�U����(�I���{�^�����������܂œ����Ȃ�) */
		SELECT_CONT_WAIT(     "SELECT_CONT_WAIT"),
		
		/** ���[�U�����̌㏈�� */
		SELECT_CONT_WAIT_POST("SELECT_CONT_WAIT_POST"),
		
		/** 1���P�ʏ����̌㏈�� */
		SELECT_CONT_POST(     "SELECT_CONT_POST"),
		
		/** �I���̑O���� */
		SELECT_POST("SELECT_POST"),
		
		/** �ړ��̑O���� */
		MOVE_PRE(        "MOVE_PRE"),
		
		/** �l�P�ʈړ��̑O���� */
		MOVE_PERSOPN_PRE("MOVE_PERSON_PRE"),

		/** �l�P�ʈړ� */
		MOVE_PERSON(     "MOVE_PERSON"),
		
		/** �l�P�ʈړ��̌㏈�� */
		MOVE_PERSON_POST("MOVE_PERSON_POST"),

		/** �ړ��̌㏈�� */
		MOVE_POST("MOVE_POST"),
		
		/** �v���[���[������ */
		END_GAME_OVER("END_GAME_OVER"),
		
		/** ���E���e���B�����ꂽ */
		END_ONE("END_ONE"),
		
		/** �I�� */
		END("END");

		/**
		 * �R���X�g���N�^ 
		 * @param name ���O
		 */
		private NOW(String name) {
			this.name = name;
		}
		/** �������i�C�x���g�p�j */
		public final String name;
	}
	
	/** �{�^���I�������̎�� */ 
	public enum ACTION_TYPE {
		/** ��~���� */
		IGNRE,
		
		/** �I���{�^�����������Ƃ� */
		TURN_END,
		
		/** stop�{�^�����������Ƃ� */
		EXTRNAL_TURNEND,
		
		/** auto�{�^�����������Ƃ� */
		EXTRNAL_TURNSTART,
		
		/** �X�s�[�h�A�b�v�{�^�����������Ƃ� */
		SPEED_UP
	}

	/** �R���X�g���N�^ */
	public State() {
		this.targetLocationList = new ArrayList<EventLocation>();
		this.personMoveList =  new ArrayList<EventPerson>();
		this.contryMoveList = new ArrayList<EventContry>();
		this.messageList = new ArrayList<Message>();
		this.bQueue = new LinkedBlockingDeque<Runnable>();
	}
	
	/** ���������� */
	public void run() {
		//
		// ���ʏ�Ԑݒ�
		now = NOW.START_PRE;
		turnEndFlag = false;
		skipTurnFlag = false;
		speedUpFlag = 0;
	    d = new XMLData();
		//
	    fInvoker = new FrameInvokMediator(State.this);
	    fInvoker.createFrameToSwing();
		//
		// �����`��
	    fInvoker.showBaseToSwing(this.bigEvent,false,false);
		//
		//
		turn = 0;
		//
		//
		while (true) {
			long speed = 100;
			//
			if (!messageList.isEmpty()) {
				// �C�x���g����
				speed = 3000;
				startEvent();
			} else {
				//
				/// �C�x���g�ȊO�̏���
				mainLoop();
				//
				//�C�x���g�̃`�F�b�N
				findEvent();
				//
				if ((now == NOW.SELECT_CONT_WAIT)
						|| (now == NOW.START)
						|| (now == NOW.END)) {
					speed = Integer.MAX_VALUE;
				}
			}
			
			// ���x�̕ύX
			if ((200 < speed) && (now != NOW.END)) {
				if (speedUpFlag <= 0) {
					speed = Integer.MAX_VALUE;
				} else if (speedUpFlag <= 1) {
					speed = 50000;
				} else if (speedUpFlag <= 2) {
					speed = 1000;
				} else if (speedUpFlag <= 3) {
					speed = 100;
				} else if (speedUpFlag <= 4) {
					speed = 10;
				}
			}
			try {
				//
				// �҂������鏈��
				Runnable data = bQueue.poll(speed,TimeUnit.MILLISECONDS);
				if (data != null) {
					data.run();
					boolean showTableEtc = this.getNow() == NOW.SELECT_CONT_WAIT;
					fInvoker.showBaseToSwing(this.bigEvent,showTableEtc,showTableEtc);
				}
			} catch (InterruptedException e) {
				/*EMPTY*/
			}
    	}
	}
	
	/**
	 * ���C�����[�v
	 */
	public void mainLoop() {
		// ���̑��̃`�F�b�N
		switch (this.getNow()) {
		case START_PRE:
			now = NOW.START; 
			break;
		case START:
			now = NOW.START_POST; 
			break;
		case START_POST:
			now = NOW.MAP_PRE; 
			break;
		case MAP_PRE:
			now = NOW.MAP_CREATE; 
			break;
		case MAP_CREATE:
			createMap();
			timer = 0;
			now = NOW.MAP_MOVE; 
			break;
		case MAP_MOVE:
			timer++;
			fInvoker.changeMapToSwing();
			fInvoker.showBaseToSwing(this.bigEvent,false,false);
			if (50 < timer) {
				now = NOW.MAP_POST;
			}
			break;
		case MAP_POST: // �C�x���g�ň����|����p
			now = NOW.SELECT_PRE; 
			break;
		case SELECT_PRE:
			turn++;
			addMessage(Message.TYPE.NAL,"�Ȃ�݂�u�^�[�� " + turn + " �J�n���܂��v");
			this.contryMoveList.addAll(d.getContryList());
			myContry = null;
			now = NOW.SELECT_CONT_PRE; 
			break;
		case SELECT_CONT_PRE:
			// ���l�`�F�b�N
			if (!this.contryMoveList.isEmpty()) {
				myContry = this.contryMoveList.remove(rand.nextInt(this.contryMoveList.size()));
				myPerson = null;
				if (!myContry.isAlive()) {
					// �����҂����Ȃ�
					if (createSecondLife()) {
						// �����ł���
						now = NOW.SELECT_CONT;
					} else {
						// �����ł��Ȃ�
						now = NOW.SELECT_CONT_POST;
					}
				} else {
					now = NOW.SELECT_CONT;
				}
			} else {
				// �S�Ă̍����`�F�b�N����
				this.now = NOW.SELECT_POST;
			}
			break;
		case SELECT_CONT:
			// �^�[���擪
			fInvoker.showMapToSwing();
			if (myContry.isPc()) {
				addMessage(Message.TYPE.FRIEND,"�Ȃ�݂�u" + myContry.getTitle() + "�̃^�[������v");
			} else {
				addMessage(Message.TYPE.ENEMY,"�Ȃ�݂�u" + myContry.getTitle() + "�̃^�[�����J�n���܂��v");
			}
			//
			// �s�������{��
			thinkingTime();
			//
			if (myContry.isPc() && (!skipTurnFlag)) {
				now = NOW.SELECT_CONT_WAIT_PRE;
			} else {
				this.targetLocationList.clear();
				this.targetLocationList.addAll(myContry.getLocationSet());
				now = NOW.SELECT_CONT_POST;
			}
			break;
		case SELECT_CONT_WAIT_PRE:
			// �\�f�[�^�̍X�V
			fInvoker.updateTableValueToSwing();
			now = NOW.SELECT_CONT_WAIT;
			// ��ԕ\��
			fInvoker.showBaseToSwing(this.bigEvent,true,true);
			break;
		case SELECT_CONT_WAIT:
			if (turnEndFlag) {
				turnEndFlag = false;
				now = NOW.SELECT_CONT_WAIT_POST;
			}
			break;
		case SELECT_CONT_WAIT_POST:
			if (myContry.isPc()) {
				addMessage(Message.TYPE.FRIEND,"�Ȃ�݂�u" + myContry.getTitle() + "�̃^�[�����I���܂����v");
			} else {
				addMessage(Message.TYPE.ENEMY,"�Ȃ�݂�u" + myContry.getTitle() + "�̃^�[�����I���܂����v");
			}
			this.now = NOW.SELECT_CONT_POST;
			break;
		case SELECT_CONT_POST:
			if (this.d.getContryList().stream()
					.filter(cont->cont.isPc())
					.mapToInt(cont->cont.getLocationSet().size()).sum()
					<= 0){
				// �S�����ƂŎx�z�n�悪�P���Ȃ�
				this.now = NOW.END_GAME_OVER;
			} else if (this.d.getContryList().stream()
					.filter(cont-> 0 < cont.getLocationSet().size())
					.mapToInt(cont->1).sum() <= 1) {
				// �c�����P�������Ȃ��̂Ȃ琢�E�����I���I
				this.now = NOW.END_ONE;
			} else {
				// ���̍ŏ��ɖ߂�
				this.now = NOW.SELECT_CONT_PRE;
			}
			break;
		case SELECT_POST:
			this.now = NOW.MOVE_PRE;
			break;
		case MOVE_PRE:
			now = NOW.MOVE_PERSOPN_PRE;
			List<EventPerson> personList = this.d.getContryList().stream()
			.flatMap(contry->contry.getPersonList().stream())
			.filter(e->e.isAlive())
			.collect(Collectors.toList());
			this.personMoveList.addAll(personList);
			//
			addMessage(Message.TYPE.NAL,"�Ȃ�݂�u���ׂĂ̍��̍s�����j�����肳��܂����v");
			break;
		case MOVE_PERSOPN_PRE:
			myPerson = personMoveList.remove(rand.nextInt(personMoveList.size()));
			now = NOW.MOVE_PERSON;
			break;
		case MOVE_PERSON:
			invokePerson();
			now = NOW.MOVE_PERSON_POST;
			break;
		case MOVE_PERSON_POST:
			this.targetLocationList.clear();
			if (personMoveList.isEmpty()) {
				now = NOW.SELECT_PRE;
			} else {
				now = NOW.MOVE_PERSOPN_PRE;
			}
			break;
		case MOVE_POST:
			addMessage(Message.TYPE.NAL,"�Ȃ�݂�u�S���̍s�����I���܂����v");
			this.now = NOW.SELECT_PRE;
			break;
		case END_GAME_OVER:
		case END_ONE: // �����ŃC�x���g�������|����
			this.now = NOW.END;
			break;
		case END:
			// �\�f�[�^�̍X�V
			fInvoker.updateTableValueToSwing();
			// ���ʕ\��(�����ɂ��ĕ\��)
			myContry = this.d.getContryList().get(0);
			myContry.setPc(true);
			fInvoker.showBaseToSwing(this.bigEvent,true,false);
			break;
		default:
		}
		//
	}
	
	/**
	 * �C�x���g���b�Z�[�W��\��������
	 */
	public void startEvent() {
		Message messageClass = messageList.remove(0);
		String message = messageClass.getMessage();
		//
		String title = "�^�[��" + turn;
		EventPerson selectPerson = myPerson;
		if (myPerson == null) {
			if ((myContry != null) && (!myContry.getArivePersonList().isEmpty())) {
				selectPerson = myContry.getArivePersonList().get(0);
			}
		}
		if (selectPerson != null) {
			title = title + "/" + selectPerson.getTitle();
			message = message.replace("%a",selectPerson.getTitle());
			if (selectPerson.getSelection() != null) {
				title = title + "/" + selectPerson.getSelection().title;
				message = message.replace("%d",selectPerson.getSelection().title);
			}
		}
		if (myContry != null) {
			title = title + "/" + myContry.getTitle();
			message = message.replace("%b",myContry.getGobi());
			message = message.replace("%c",myContry.getTitle());
		}
		//
		this.bigEvent = (messageClass.getBigEventFileName() != null)
				&& (!messageClass.getBigEventFileName().equals(""));
		fInvoker.addMessageToSwing(
				messageClass.getType(),
				title,
				message,
				messageClass.getMiniEventFileName(),
				messageClass.getBigEventFileName());
		fInvoker.showBaseToSwing(this.bigEvent,false,false);
	}
	
	/** ����̎擾
	 * @return ����
	 */
	public State.NOW getNow() {
		return this.now;
	}
	/**
	 * �}�E�X���N���b�N���ꂽ 
	 * @param type �N���b�N���ꂽ�}�E�X�C�x���g�̓��e
	 */
	public void action(ACTION_TYPE type) {
		try {
			bQueue.put(()->{
				if (type == ACTION_TYPE.SPEED_UP) {
					speedUpFlag = speedUpFlag < 3 ? speedUpFlag + 1: 0;
				} else if (type == ACTION_TYPE.IGNRE) {
					speedUpFlag = 0;
				} else if (type == ACTION_TYPE.EXTRNAL_TURNSTART) {
					this.skipTurnFlag = false;
				} else if (type == ACTION_TYPE.EXTRNAL_TURNEND) {
					this.skipTurnFlag = true;
				} else {
					this.turnEndFlag = true;
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �I�����̔��f�{�^���������ꂽ
	 * @param selectRate ���[�g���
	 */
	public void action(int[] selectRate) {
		try {
			bQueue.put(()->{
				for (int j = 0 ; j < selectRate.length ; j++) {
					myContry.selectRate[j] = selectRate[j];
				}
				//
				// �ēx�w�K
				thinkingTime();
				//
				// �e�[�u���ɔ��f
				fInvoker.updateTableValueToSwing();
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �W�I�n�悪�I�����ꂽ 
	 * @param targetLocationList �W�I�n��
	 * @param person �l
	 */
	public void action(List<EventLocation> targetLocationList,EventPerson person) {
		try {
			bQueue.put(()->{
				if (targetLocationList != null) {
					this.targetLocationList.clear();
					this.targetLocationList.addAll(targetLocationList);
				}
				this.myPerson = person;
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���݂̃J����� 
	 * @return ���݂̍����
	 */
	public EventContry getMyContry() {
		return myContry;
	}
	/**
	 * �C�x���g�̌��� 
	 * @return �}�b�`�����C�x���g
	 */
	protected EventSinario findEvent() {
		if (now == NOW.MAP_MOVE) {
			return null; // �����ł̓C�x���g�͐�΂ɋN���Ȃ�
		}
		int priority = Integer.MAX_VALUE;
		EventSinario sinario = null;
		for (EventSinario child : d.getSinarioList()) {
			boolean sinarioNow = true;
			//
			// �e�`�F�b�N
			Event parent = child.getParentEvent();
			if (parent != null) {
				if ((parent instanceof EventContry) &&
						((myContry == null) || (parent != myContry))) {
					sinarioNow = false;
				} else if ((parent instanceof EventPerson) &&
						((now != NOW.MOVE_PERSON_POST) || (myPerson == null) || (parent != myPerson))) {
					sinarioNow = false;					
				} else if (parent instanceof EventLocation) {
					if  ((this.myPerson == null)
							|| (this.myPerson.getToLocation() == null)
							|| (this.myPerson.getToLocation() != parent)) {
						sinarioNow = false;	
					}
				} else if ((parent instanceof EventSinario)
							&& (((EventSinario)parent).getCount() <= 0)) {
					// �e�̃V�i���I���܂����s����Ă��Ȃ�
					sinarioNow = false;
				}
			}
			//
			// �����`�F�b�N
			for (IFData ifData : child.getIfDataList()) {
				if (!sinarioNow) {
					break;
				}
				switch (ifData.getName()) {
				case "1ST":
					// �����F���s�񐔁i�O�̂Ƃ��������肦�Ȃ��͂�)
					if (child.getCount() != 0) {
						sinarioNow = false;
					}
					break;
				case "CONT_PRI":
					// ���`�F�b�N
					if ((myContry != null)
						&& (!myContry.isPc())) {
						sinarioNow = false;
					}
					break;
				case "TURN":
					if (ifData.getValueInt() != this.turn) {
						sinarioNow = false;
					}
					break;
				case "NOW":
					// ���s����
					String target = this.getNow().name;
					if (!ifData.getValue().equals(target)) {
						sinarioNow = false;
					}
					break;
				case "SELECT":
					// �����ɏ]���ăC�x���g����������
					if (now == NOW.MOVE_PERSON_POST) {
						if ((myPerson.getSelection() == null)
								|| (!ifData.getValue().equals(myPerson.getSelection().signature))) {
							sinarioNow = false;
						}
					} else {
						// �l�̍s�����I��������ȊO��false
						sinarioNow = false;
					}
					break;
				default:
					System.out.println("unkown if name=" + ifData.getName());
				}
			}
			// 
			if (sinarioNow) {
				int priNow = child.getPriority();
				if (priNow < priority) {
					priority = priNow;
					sinario = child;
				}
			}
		}
		//
		// �V�i���I�̎��s
		if (sinario != null) {
			sinario.setCount(sinario.getCount()+1);
			String img = sinario.getImage();
			sinario.getMessage().stream().forEachOrdered(message->addMessage(message,null,img));
			if (sinario.selectRate != null) {
				for (int i = 0 ; i < myContry.selectRate.length ; i++) {
					myContry.selectRate[i] = sinario.selectRate[i];
				}
			}
		}
		return sinario;
	}
	
	/** �����}�b�v�̐��� */
	protected void createMap() {
		d.getLocationSet().forEach(a->{
			a.setX(rand.nextInt(ImageFactory.WINDOW_MAP_X -6)+3);
			a.setY(rand.nextInt(ImageFactory.WINDOW_MAP_Y-6)+3);
		});
		//
		// �K���n���ڑ�����
		Set<EventLocation> toEventList = new TreeSet<EventLocation>();
		List<EventLocation> fromEventList = new LinkedList<EventLocation>(d.getLocationSet());
		while (!fromEventList.isEmpty()) {
			if (toEventList.isEmpty()) {
				EventLocation fromTarget = fromEventList.remove(0);
				toEventList.add(fromTarget);
				fromEventList.remove(fromTarget);
			} else {
				EventLocation fromTarget = null;
				EventLocation toTarge = null;
				int range = Integer.MAX_VALUE;
				for (EventLocation fromEvent : fromEventList) {
					for (EventLocation toEvent : toEventList) {
						int nowRange = fromEvent.getRange(toEvent);
						if (nowRange < range) {
							range = nowRange;
							fromTarget = fromEvent;
							toTarge = toEvent;
						}
					}
				}
				fromTarget.addLocation(toTarge);
				toTarge.addLocation(fromTarget);
				toEventList.add(toTarge);
				toEventList.add(fromTarget);
				fromEventList.remove(toTarge);
				fromEventList.remove(fromTarget);
			}
		}
		//�n��ɋZ�p�͂�z�z����
		for (EventLocation fromEvent : d.getLocationSet()) {
			fromEvent.setTecnic(rand.nextInt(80) + 20);
		}
		//�P�����ڑ�����
		for (EventLocation fromEvent : d.getLocationSet()) {
			//if (3 <= fromEvent.getLocationList().size()) {
			//	continue;
			//}
			EventLocation fromTarget = null;
			EventLocation toTarge = null;
			int range = Integer.MAX_VALUE;
			for (EventLocation toEvent : d.getLocationSet()) {
				if ((fromEvent == toEvent)
						|| fromEvent.getLocationSet().contains(toEvent)) {
					continue;
				}
				int nowRange = fromEvent.getRange(toEvent);
				if (nowRange < range) {
					range = nowRange;
					fromTarget = fromEvent;
					toTarge = toEvent;
				}
			}
			if (fromTarget != null) {
				fromTarget.addLocation(toTarge);
				toTarge.addLocation(fromTarget);
			}
		}
		//
		// ���Ƃ�K���ɔz�u����
		List<EventContry> contryList = new ArrayList<EventContry>(this.d.getContryList());
		List<EventLocation> locationList  = new ArrayList<EventLocation>(this.d.getLocationSet());
		while ((!contryList.isEmpty()) && (!locationList.isEmpty())) {
			EventLocation location = locationList.remove(rand.nextInt(locationList.size()));
			EventContry contry = contryList.remove(0);
			//
			// ���ɓK���Ȓn���z�u����
			contry.addLocation(location);
			//
			// �l�ɒn���z�z����
			contry.getPersonList().stream().forEach(ev->ev.setLocation(location));
		}
		//
		// �F�D�l��K���ɔz�z����
		this.d.getContryList().stream().forEachOrdered(cont->{
			cont.setLike(new int[this.d.getContryList().size()]);
			for (int i= 0 ; i < cont.getLike().length ; i++) {
				cont.getLike()[i] = rand.nextInt(80) + 20;
			}
		});
	}
	
	/** ���Ǝv�l���� */
	protected void thinkingTime() {
		//
		//�x�z�n��̍��̐��ɋZ�p�𑫂��������������𑝂₷
		int money = myContry.getMoney()
				+ (myContry.getLocationSet().stream().mapToInt(e->e.getTecnic()).sum()/10);//�␳�t��
		myContry.setMoney(money);
		//
		// �����Ă���l�Ƀ����_���ɍs����z�z
		this.myContry.getArivePersonList().stream().forEachOrdered(person->thinkingTimeOne(person));
	}
	
	/**
	 * �l�ЂƂ�̍s������ 
	 * @param person �ΏۂƂȂ�l
	 */
	protected void thinkingTimeOne(EventPerson person) {
		//
		// �I�����̌���
		int sum = 0;
		for (int i = 0 ; i < myContry.selectRate.length ; i++) {
			sum += myContry.selectRate[i];
		}
		// �I�����̍��v��0�̏ꍇ�A���l��z�z���Ȃ���
		if (sum <= 0) {
			sum = 0;
			for (int i = 0 ; i < myContry.selectRate.length ; i++) {
				int plus = rand.nextInt(9) + 1;
				myContry.selectRate[i] = plus;
				sum += plus;
			}
		}
		// ���v�l������
		final int fSum = sum;
		//
		int randInt = rand.nextInt(fSum);
		int targetSelect = 0;
		int randIntSum = 0;
		for (int i = 0 ; i < myContry.selectRate.length ; i++) {
			randIntSum += myContry.selectRate[i];
			if (randInt < randIntSum) {
				targetSelect = i;
				break;
			}
		}
		//
		SelectionData sd = SelectionData.DATA[targetSelect];
		//
		// ��������̐ݒ�
		if ((person.getMp() + sd.mp <= 0) && (sd.mp < 0)) {
			// mp������邩�`�F�b�N
			// ����������̂��͎̂��s�ł���
			// MP���Ȃ��ꍇ�͋x�e����
			sd = SelectionData.getSignatureToSelectionData(SelectionData.SG.SL);
		} else if (!person.getContry().getLocationSet().contains(person.getLocation())) {
			// �G���ɂ���Ȃ�
			// �����I�ɐ키��I��
			sd = SelectionData.getSignatureToSelectionData(SelectionData.SG.FT);
		} else if (person.getContry().getMoney() - (50 * person.getContry().getArivePersonList().size()) <= 0) {
			// ����������邩�`�F�b�N(���Ɨ\�Z���l����100��؂������펖�Ԃ��낤)
			sd = SelectionData.getSignatureToSelectionData(SelectionData.SG.HN);
		}
		//
		// ������e
		person.setSelection(sd);
		//
		// �ړ��挈��
		d.invokeMovePersion(person);

	}
	
	
	/**
	 * �l�ЂƂ�̎��ۂ̍s��
	 * @param person
	 */
	protected void invokePerson() {
		// ������ݒ�
		this.myContry = myPerson.getContry();
		this.targetLocationList.clear();
		this.targetLocationList.add(myPerson.getLocation());
		//
		if (!myPerson.isAlive()) {
			addMessage("%a�͂�������ł���B",null,null);
			return;
		}
		// ���܂łōs������
		// �s�����e���C�x���g�ɒǉ�
		addMessage(
				myPerson.getContry().getTitle() + "����:"
				+ myPerson.getTitle() + "�́u"
				+ myPerson.getSelection().title + "�v����%b");
		//
		// mp�̏���
		int mp = myPerson.getStr() < 5 ?  myPerson.getStr() : 5; //�␳
		mp = mp + myPerson.getSelection().mp ;
		if ((myPerson.getMp() + mp <= 0) && (mp < 0)) {
			// ���g��MP���O�ȉ��ł��s��������������̂��͎̂��s�ł���
			addMessage("%a�ɂ͍s�����邽�߂�MP���Ȃ�����%b");
			myPerson.setMp(myPerson.getMp() + 10); // �񕜂����Ă�����
			return;
		}
		//
		// ���z�G���̐ݒ�
		EventContry enemyContry = this.d.getEnemyContry(myContry);
		if (enemyContry == null) {
			addMessage("%a�ɓG�͂��Ȃ�����%b");// �������E�������Ă܂񂪂�
			return;
		}
		//
		// enemy�̗F�D�x�́H
		int enemyLike = enemyContry.getLike()[myContry.getContryNumber()];
		//
		// �p�����[�^�X�V�t�F�[�Y
		int yuukou;
		int tekiTekiYukou;
		int tec;
		int money;
		if (0 < enemyLike) {
			// �F�D�I�ȂƂ�
			yuukou = this.myPerson.getSelection().yuukouA;
			tekiTekiYukou = this.myPerson.getSelection().tekiTekiYukouA;
			tec = this.myPerson.getSelection().tecA;
			money = this.myPerson.getSelection().moneyB;
		} else {
			// �e���݂������Ă��Ȃ��Ƃ�
			yuukou = this.myPerson.getSelection().yuukouB;
			tekiTekiYukou = this.myPerson.getSelection().tekiTekiYukouB;
			tec = this.myPerson.getSelection().tecB;
			money = this.myPerson.getSelection().moneyB;
		}
		// ���������邩�`�F�b�N
		if ((myContry.getMoney() <= 0) && (money < 0)) {
			// ��������������ŁA���A�������}�C�i�X�ɂȂ�����̂Ƃ���NG
			addMessage("%a�ɂ͍s�����邨�����Ȃ�����%b");
			return;
		}
		//
		// �܂��͈ړ�
		EventLocation toLocation = this.myPerson.getToLocation();
		if (toLocation != null) {
			addMessage("%a�� " + this.myPerson.getLocation() + " ���� " + toLocation + " �ֈړ�����");
			this.myPerson.setLocation(toLocation);
			this.targetLocationList.add(toLocation);
		}
		//
		//�U���t�F�[�Y
		// �܂��G��T��
		List<EventPerson> vsPersonList =
				d.getContryList().stream()
				.filter(contry->contry != myPerson.getContry()) // �����͏���
				.flatMap(cont->cont.getArivePersonList().stream())
				.filter(person->0 < person.getHp())
				.filter(person->person.getLocation() == myPerson.getLocation())
				.collect(Collectors.toList());
		if ( ! vsPersonList.isEmpty()) {
			// �G�̐l��������Ƃ��ア���T��
			EventPerson vsPerson = vsPersonList.get(0); // �I���Ȃ��΍�
			for (EventPerson targetPerson : vsPersonList) {
				if ((0 < targetPerson.getHp())
						&& (targetPerson.getHp() <= vsPerson.getHp())) {
					vsPerson = targetPerson; // �ł�HP�̒Ⴂ���D�悵�Ē@��
				}
			}
			// �G�̎����̗F�D�x��-2���炷
			vsPerson.getContry().getLike()[myPerson.getContry().getContryNumber()]
					+= - 2;

			// ���̒n��̋Z�p�� ���炷
			myPerson.getLocation().setTecnic(myPerson.getLocation().getTecnic()-20);
			
			// �����ł��̏ꏊ�ɐl�����邩�m�F
			int dummage = rand.nextInt(30) + 10 + myPerson.getStr();
			vsPerson.setHp(vsPerson.getHp() - dummage);
			//
			addMessage("%a��" + vsPerson + "��" + dummage + "�̃_���[�W��^����!�c("
					+ vsPerson.getHp() + ")",ImageFactory.EVENT_ATTACK,null);
			int tuika = myPerson.getSelection().move;
			if ((1 <= tuika) && (tuika <= 4)) {
				tuika = rand.nextInt(20) + 1 + myPerson.getStr()*2;
				vsPerson.setHp(vsPerson.getHp() - dummage);
				addMessage("�U���I���ɂ��ǉ�" + tuika + "�̃_���[�W��^����!�c("
						+ vsPerson.getHp() + ")",ImageFactory.EVENT_ATTACK,null);
			}
			// ���񂾔���
			if (!vsPerson.isAlive()) {
				addMessage(
						vsPerson.getContry() + "����:"
								+ vsPerson + "�͎���",ImageFactory.EVENT_DEAD,null);
				//
				// ������x�G��T��(���̓y�n�̓G���S�ł��Ă��邩�m�F)
				vsPersonList =
						d.getContryList().stream()
						.filter(contry->contry != myPerson.getContry()) // �����͏���
						.flatMap(cont->cont.getArivePersonList().stream())
						.filter(person->person.isAlive())
						.filter(person->person.getLocation() == myPerson.getLocation())
						.collect(Collectors.toList());
			}
		}
		if (vsPersonList.isEmpty()) {
			//�������Ȃ����
			if (!myPerson.getContry().getLocationSet().contains(myPerson.getLocation())) {
				// �����ɂ��̓y�n���܂�ł��Ȃ��Ȃ�ȉ������s
				//
				List<EventContry> enemyContryList = d.getContryList().stream()
						.filter(contry->contry != myPerson.getContry()) // �����͏���
						.filter(cont->cont.getLocationSet().contains(myPerson.getLocation()))
						.collect(Collectors.toList());
				if (enemyContryList.isEmpty()) {
					// ��������Ȃ�
					addMessage(myPerson.getTitle() + "��" + myPerson.getLocation() + "��̓y�Ɛ錾����%b",ImageFactory.EVENT_VOTE,null);
					myPerson.getContry().addLocation(myPerson.getLocation());
				} else {
					enemyContryList.stream().forEachOrdered(toContry->{
						// ��������Ȃ�
						addMessage("%a��" + myPerson.getLocation() + "��" + toContry + "����D�悵��",ImageFactory.EVENT_GET,null);
						myPerson.getContry().getLocationSet().add(myPerson.getLocation());
						toContry.getLocationSet().remove(myPerson.getLocation());
					});
				}
			}
		}
		//
		// ���Y�t�F�[�Y(������)
		if ((myPerson.getSelection().move == 5)
				&& (80 - myPerson.getStr() < myPerson.getLocation().getTecnic())) {
			// �Z�p�͂��Ⴂ�ƒ��Ԃ͑����܂���
			List<EventPerson> friendList =
					myPerson.getContry().getPersonList().stream()
					.filter(son->!son.isAlive())
					.collect(Collectors.toList());
			//
			//
			if (!friendList.isEmpty()) {
				EventPerson vsPerson = friendList.get(rand.nextInt(friendList.size()));
				vsPerson.setHp(50);
				vsPerson.setMp(30);
				vsPerson.setLocation(myPerson.getLocation());
				addMessage("%a��" + vsPerson + "��" + myPerson.getLocation() + "�Œ��Ԃɂ���");
				//
				// �Z�p�͂͐l���������������̂Ō���܂�
				// ���̒n��̋Z�p�����炷
				myPerson.getLocation().setTecnic(myPerson.getLocation().getTecnic()-40);
			}
		}
		
		//
		// �X�e�[�^�X�ύX
		// ������HP����
		myPerson.setHp(myPerson.getHp() + this.myPerson.getSelection().hp);
		//
		// ������MP����
		myPerson.setMp(myPerson.getMp() + mp); 
		// ����
		myContry.setMoney(myContry.getMoney() + money);
		//
		// �Z�p��
		myPerson.getLocation().setTecnic(myPerson.getLocation().getTecnic() + tec);
		//
		// �F�D�x����
		// // �G�̗F�D�x�̒���
		// // �������猩����
		for (EventContry targetContry: d.getContryList()) {
			if (targetContry == myPerson.getContry()) {
				// �G�̓G���������𒲐��i���������Z)
				targetContry.getLike()[enemyContry.getContryNumber()]
						+= (yuukou / 2);
			} else if (targetContry == enemyContry){
				targetContry.getLike()[myPerson.getContry().getContryNumber()]
						+= yuukou;
			} else {
				// �������猩�āA�G�����F�D�x�ɉ����Ď����𒲐�
				// �����̓G���̗F�D�x
				int yuuko = targetContry.getLike()[enemyContry.getContryNumber()];
				if (0 < yuuko) {
					// �G�����D���ȍ�(�G���̖���)
					targetContry.getLike()[myPerson.getContry().getContryNumber()]
							-= tekiTekiYukou;
				} else {
					// �G���̓G
					targetContry.getLike()[myPerson.getContry().getContryNumber()]
							+= tekiTekiYukou;
				}
			}
		}
	}

	/**
	 * �Z�J���h���C�t�̃`�F�b�N 
	 * @return ���������邱�Ƃ��ł����̂ł���� true
	 */
	protected boolean createSecondLife() {
		if (!myContry.getLocationSet().isEmpty()) {
			// �܂��̒n������
			EventLocation location =
					myContry.getLocationSet().stream()
					.collect(Collectors.toList()).get(0);
			addMessage(Message.TYPE.NAL,"�Ȃ�݂�u" + myContry.getTitle() + "���S�ł��܂��̂�"
					+ location + "�ŕ��������܂����v",
					ImageFactory.EVENT_CREATE,null);
			addMessage(Message.TYPE.NAL,"�Ȃ�݂�u�܂��܂������܂���[�v",ImageFactory.EVENT_CREATE,null);
			EventPerson person = myContry.getPersonList().get(0);
			person.setHp(1);
			person.setMp(1);
			person.setLocation(location);
			return true;
		} else {
			// �󂢂Ă���y�n��T��
			List<EventLocation> contryLocationList =
					this.getData().getContryList().stream()
					.flatMap(cont->cont.getLocationSet().stream())
					.collect(Collectors.toList());
			List<EventLocation> nonContryLocationList =
					new ArrayList<EventLocation>(this.getData().getLocationSet());
			nonContryLocationList.removeAll(contryLocationList);
			if (!nonContryLocationList.isEmpty()) {
				EventLocation location = nonContryLocationList.get(rand.nextInt(nonContryLocationList.size()));
				addMessage(Message.TYPE.NAL,
						"�Ȃ�݂�u" + myContry.getTitle() + "���S�ł��܂��̂ŐV����"
						+ location.getTitle()
						+ "�ŕ��������܂����v",
						ImageFactory.EVENT_CREATE,null);
				addMessage(Message.TYPE.NAL,"�Ȃ�݂�u�y�n�̗L�����p�ł��ˁv",ImageFactory.EVENT_CREATE,null);
				myContry.getLocationSet().add(location);
				EventPerson person = myContry.getPersonList().get(0);
				person.setHp(1);
				person.setMp(1);
				person.setLocation(location);
				return true;
			}
		}
		return false;
	}

    /**
     * �W�I�̒n��B
     * �}�b�v�\���Ŏ�Ɏg����
     * @return �n����
     */
    public List<EventLocation> getTargetLocationList() {
    	return this.targetLocationList;
    }
    /**
     * �l�̏��  
     * @return ���ݍ�ƒ��̐l�̏��
     */
    public EventPerson getMyPerson() {
    	return this.myPerson;
    }
    
	/**
	 * XML�f�[�^�̎擾 
	 * @return �n����
	 */
    public XMLData getData() {
    	return d;
    }
    
    /**
     * �X�s�[�h�A�b�v�t���O�̎擾 
     * @return ����
     */
    public int getSpeedUpFlag() {
    	return this.speedUpFlag;
    }
    /**
     * �ʏ탁�b�Z�[�W�̐ݒ� 
     * @param message ���b�Z�[�W 
     */
    public void addMessage(String message) {
    	addMessage(message,null,null);
    }
    /**
     * �ʏ탁�b�Z�[�W�̐ݒ� 
     * @param type ���b�Z�[�W���
     * @param message ���b�Z�[�W
     */
    public void addMessage(Message.TYPE type,String message) {
    	addMessage(type,message,null,null);
    }
    /**
     * �ʏ탁�b�Z�[�W�̐ݒ� 
     * @param message ���b�Z�[�W
     * @param miniEventFile �~�j�C�x���g�摜
     * @param bigEvent �ł����C�x���g�摜
     */
    public void addMessage(String message,String miniEventFile,String bigEvent) {
    	addMessage(getMessageType(),message,miniEventFile,bigEvent);
    }
    /**
     * �ʏ탁�b�Z�[�W�̐ݒ� 
     * @return ���b�Z�[�W�F
     */
    public Message.TYPE getMessageType() {
    	Message.TYPE type;
    	if (this.myPerson == null) {
    		if (this.myContry == null) {
    			type = Message.TYPE.NAL;
    		} else if (this.myContry.isPc()) {
        		type = Message.TYPE.FRIEND;
        	} else {
        		type = Message.TYPE.ENEMY;
        	}
    	} else if (this.myPerson.getContry().isPc()) {
    		type = Message.TYPE.FRIEND;
    	} else {
    		type = Message.TYPE.ENEMY;
    	}
    	return type;
    }
    /**
     * �ʏ탁�b�Z�[�W�̐ݒ� 
     * @param type ���
     * @param message ���b�Z�[�W
     * @param miniEventFile �~�j�C�x���g�摜
     * @param bigEvent �ł����C�x���g�摜
     */
    public void addMessage(Message.TYPE type,String message,String miniEventFile,String bigEvent) {
    	messageList.add(new Message(type,message,miniEventFile,bigEvent));
    }
	
	/** �^�C�}�[ */
	private int timer = 0;

    /** ���������p */
    private final Random rand = new Random();
    
	/** �t���[������ */
	private FrameInvokMediator fInvoker;

	/** �҂��p */
	private final BlockingQueue<Runnable> bQueue;

	/** ���݂̏�� */
	private NOW now;
	
	/** XML�f�[�^ */
	private XMLData d;

	/** �W�I���Ƃ̕\��(�\���p) */
	private final List<EventLocation> targetLocationList;
	
	/** ���݂̃��b�Z�[�W */
	private final List<Message> messageList;
	
	/** �l�̍s�����X�g(�c���Ă���l) */
	private final List<EventPerson> personMoveList;
	
	/** �^�[���G���h�t���O */
	private boolean turnEndFlag;
	
	/** �S�̂̃^�[���ԍ� */
	private int turn;
	
	/** ���̃^�[���ԍ� */
	private final List<EventContry> contryMoveList;
	
	/** ����� */
	private EventContry myContry;

	/** �l����� */
	private EventPerson myPerson;
	
	/** �X�s�[�h�A�b�v */
	private int speedUpFlag;

	/** �X�L�b�v�t���O */
	private boolean skipTurnFlag;
	
	/** �r�b�O�C�x���g�J�n�t���O */
	private boolean bigEvent;
	
}
