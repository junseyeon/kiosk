import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * @author Seyeon Jun Java Team Project 2021-05-27 키오스크 만들기
 */
public class Restaurant extends Thread {

	String line = "";
	int count = 0;
	int payment = 0;
	int ordernum = 1;
	int durationTime = 0;
	String customer = "상품명        수량        금액\n";
	int revenue = 0;
	int menuSale[] = { 0, 0, 0, 0, 0, 0 }; // 차트를 위한 판매된 메뉴 갯수 저장 배열
	String menu[] = { "크림파스타", "오일파스타", "해물파스타", "로제파스타", "크림리조또", "해물리조또", "콜라           ", "사이다       " };
	JLabel clock;
	String now;

	public Restaurant() {

		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter("orderlist.txt"));
			bw.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		Font font_price = new Font(Font.SANS_SERIF, Font.BOLD, 13);
		Font font1 = new Font(Font.SANS_SERIF, Font.BOLD, 13);
		Font big = new Font(Font.SANS_SERIF, Font.BOLD, 15);

		JFrame frame = new JFrame("레스토랑 키오스크 만들기");
		frame.setSize(1010, 580);
		frame.setLocationRelativeTo(null); // 화면 중앙 위치하게 열림
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLayout(null);

		Panel top = new Panel();
		top.setLayout(null);
		top.setBackground(Color.white);
		top.setBounds(0, 0, 1010, 60);

		Panel left = new Panel();
		left.setBackground(Color.white);
		left.setBounds(0, 60, 700, 575);
		left.setLayout(null);

		Panel right = new Panel();
		right.setBackground(Color.white);
		right.setBounds(700, 60, 310, 575);
		right.setLayout(null);

		// top panel 추가 항목
		ImageIcon info = new ImageIcon("img/chart.png");
		JButton newpanel = new JButton("판매된 음식 그래프", info);
		newpanel.setBounds(10, 27, info.getIconWidth() + 200, info.getIconHeight());
		newpanel.setFocusPainted(false);
		newpanel.setBorderPainted(false);
		newpanel.setContentAreaFilled(false);

		newpanel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				nWindow panel = new nWindow();
				panel.addHistogramColumn(menu[0], menuSale[0], Color.RED);
				panel.addHistogramColumn(menu[1], menuSale[1], Color.YELLOW);
				panel.addHistogramColumn(menu[2], menuSale[2], Color.BLUE);
				panel.addHistogramColumn(menu[3], menuSale[3], Color.ORANGE);
				panel.addHistogramColumn(menu[4], menuSale[4], Color.MAGENTA);
				panel.addHistogramColumn(menu[5], menuSale[5], Color.CYAN);
				panel.layoutHistogram();

				JFrame frame = new JFrame("Histogram Panel");
				frame.add(panel);
				frame.setLocationByPlatform(true);
				frame.pack();
				frame.setVisible(true);
			}
		});

		JLabel title = new JLabel(ordernum + "번째 손님 음식을 주문해주세요!");
		title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
		title.setBounds(300, 0, 400, 50);

		// 현재 시간 띄우기
		clock = new JLabel();
		clock.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 14));
		clock.setBounds(400, 28, 200, 50);

		JButton call = new JButton("직원 호출", new ImageIcon("img/bell1.png")); // 버튼 생성 및 이미지 결합
		call.setBounds(850, 27, 150, 35); // 버튼 위치 설정
		call.setFocusPainted(false); // 버튼 focus에도 표시 x
		call.setBorderPainted(false); // 버튼 테두리 없음
		call.setContentAreaFilled(false); // 버튼 투명하게

		top.add(newpanel); // 왼쪽
		top.add(title); // 중앙
		top.add(clock); // 중앙
		top.add(call); // 오른쪽

		System.out.println(title.getX() + " " + title.getY());

		// 좌측 코드
		double price[] = { 14000, 15000, 13000, 16500, 16000, 17000, 2000, 2000 };
		String material[][] = { { "크림(국내산), 버섯(국내산)" }, { "오일(미국산), 바질(미국산)" }, { "조개(국내산), 새우(국내산), 홍합(국내산)" },
				{ "크림(국내산), 토마토(국내산), 치킨(국내산)" }, { "크림(국내산), 버섯(국내산), 쌀(국내산)" }, { "해물(국내산), 쌀(국내산)" }, { "탄산" },
				{ "탄산" } };
		int time[] = { 10, 10, 10, 15, 15, 15, 1, 1 };

		JButton bt[] = new JButton[menu.length]; // 메뉴이미지 버튼
		ImageIcon icon[] = new ImageIcon[menu.length]; // 메뉴이미지 버튼에 넣을 아이콘
		JLabel mname[] = new JLabel[menu.length]; // 메뉴이름
		JTextField su[] = new JTextField[menu.length]; // 메뉴 수량
		Button min[] = new Button[menu.length]; // 수량 마이너스 버튼
		Button plu[] = new Button[menu.length]; // 수량 플러스 버튼
		JButton ok[] = new JButton[menu.length]; // 주문확인 버튼

		// 버튼 설정 부분
		for (int i = 0; i < menu.length; i++) {

			// 햄버거 버튼
			bt[i] = new JButton();

			if (i < 4) {
				bt[i].setBounds(30 + i * 160, 40, 120, 110);
			} else if (i < 8 && i > 3) {
				bt[i].setBounds(30 + (i - 4) * 160, 270, 120, 110);
			}

			// 이미지버튼 설정
			bt[i].setFocusPainted(false);
			bt[i].setBorderPainted(false);
			bt[i].setContentAreaFilled(false);

			// 버튼에 이미지 넣기
			icon[i] = new ImageIcon("img/m" + (i + 1) + ".jpg");
			Image img = icon[i].getImage();
			Image changeImg = img.getScaledInstance(130, 120, Image.SCALE_SMOOTH);
			ImageIcon changeIcon = new ImageIcon(changeImg);
			bt[i].setIcon(changeIcon);

			// 메뉴 이름 , 가격
			mname[i] = new JLabel(menu[i] + " (" + price[i] / 10000 + ")");
			mname[i].setFont(font_price);
			mname[i].setBounds(bt[i].getX(), bt[i].getY() + 115, 130, 20);
			mname[i].setHorizontalAlignment(JLabel.CENTER);

			// "-" 버튼
			min[i] = new Button("-");
			min[i].setBounds(bt[i].getX() + 5, bt[i].getY() + 145, 20, 20);
			min[i].setEnabled(false);
			min[i].setBackground(Color.white);

			// 숫자 txt 버튼
			su[i] = new JTextField("0");
			su[i].setBackground(Color.white);
			su[i].setEditable(false);
			su[i].setBounds(min[i].getX() + 35, bt[i].getY() + 145, 40, 20);
			su[i].setHorizontalAlignment(JTextField.CENTER);

			// "+" 버튼
			plu[i] = new Button("+");
			plu[i].setBounds(bt[i].getX() + 90, su[i].getY(), 20, 20);
			plu[i].setEnabled(true);
			plu[i].setBackground(Color.white);

			// 확인 버튼
			ok[i] = new JButton("확인");
			ok[i].setBounds(bt[i].getX(), su[i].getY() + 25, 120, 20);
			ok[i].setEnabled(false);
			ok[i].setBackground(Color.white);
			ok[i].setBorder(BorderFactory.createLineBorder(new Color(105, 179, 38), 2));

			left.add(bt[i]);
			left.add(mname[i]);
			left.add(su[i]);
			left.add(min[i]);
			left.add(plu[i]);
			left.add(ok[i]);
		} // for

		// 오른쪽
		TextArea ta = new TextArea("", 0, 30, TextArea.SCROLLBARS_VERTICAL_ONLY);
		ta.setText("    상품명          단가         수량        합계\n\n");
		ta.setBackground(Color.white);
		ta.setEditable(false);
		ta.setFont(font1);
		ta.setBounds(0, 40, 300, 330);

		JTextField priceTotal = new JTextField("총 주문 금액 :  0원");
		priceTotal.setBackground(Color.white);
		priceTotal.setEditable(false);
		priceTotal.setBounds(0, 370, 300, 50);
		priceTotal.setFont(big);
		priceTotal.setHorizontalAlignment(JTextField.CENTER);
		priceTotal.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		JButton order = new JButton("주문하기");
		order.setBounds(0, 420, 150, 50);
		order.setBackground(Color.white);
		order.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		order.setFont(big);
		order.setForeground(new Color(105, 179, 38));

		JButton reset = new JButton("초기화");
		reset.setBounds(150, 420, 150, 50);
		reset.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		reset.setBackground(Color.white);
		reset.setFont(big);
		reset.setForeground(Color.gray);

		right.add(ta);
		right.add(priceTotal);
		right.add(order);
		right.add(reset);

		frame.add(top);
		frame.add(left);
		frame.add(right);

		for (int j = 0; j < menu.length; j++) {
			int i = j;

			// 사진(버튼) 이벤트
			bt[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String mContent = "";
					for (int j = 0; j < material[i].length; j++) {
						mContent += material[i][j]; // 메뉴의 재료를 문자열로 합함
					}
					JOptionPane.showMessageDialog(null, "주재료는\n" + mContent + "\n입니다. \n 소요시간은 " + time[i] + "분 입니다"); // 출력되는
																														// 메시지
																														// 다이얼로그
				}
			});

			// "-" 버튼 이벤트
			min[j].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (count > 0) {
						count = count - 1;
						su[i].setText(count + "");
					}
					if (count < 1) {
						min[i].setEnabled(false);
						ok[i].setEnabled(false);
					}
				}
			});

			// "+" 버튼 이벤트
			plu[j].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					count = count + 1; // 수량 추가
					su[i].setText(count + ""); // 텍스트에 수량 표시
					ok[i].setEnabled(true); // 확인 버튼 활성화
					min[i].setEnabled(true); // 마이너스 버튼 활성화
				}
			});

			// 해당 메뉴 주문버튼 이벤트
			ok[j].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					payment += price[i] * count; // 메뉴 가격 합산
					line = bt[i].getActionCommand();
					ta.append("  " + menu[i] + "    " + (int) price[i] + "원" + "      " + count + "       "
							+ (int) price[i] * count + "원\n"); // 주문내역서에 추가
					priceTotal.setText("총 주문 금액 : " + payment + "원"); // 버튼에 나타나는 총 주문 금액 결과
					ok[i].setEnabled(false); // 주문된 메뉴의 컴포넌트 초기화
					su[i].setText("0"); // 위와 동일
					durationTime += time[i] * count; // 소요시간 합함
					customer += menu[i].trim() + "    " + count + " 개       " + (int) price[i] * count + "원 \n"; // 주문
																													// 내용
																													// 저장용

					// 차트를 그리기 위한 카운트
					if (i < 6) {
						menuSale[i] += count;
					}

					count = 0; // 다시 해당 메뉴 수량을 0으로 리셋
				}
			});

		} // for문

		// 최종 주문버튼 이벤트
		order.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						customer + "> 총 " + payment + "원 입니다. \n\n예상 소요시간은 " + durationTime + "분 입니다.\n이용해주셔서  감사합니다.");
				revenue += payment; // 음식점의 수익 금액 누적
				for (int i = 0; i < menu.length; i++) { // 메뉴 항목 초기화
					ok[i].setEnabled(false);
					min[i].setEnabled(false);
					su[i].setText("0");
				}
				try { // BufferedWriter를 사용하여 버퍼로 파일 입력 // 입출력임으로 예외 처리 필요
					BufferedWriter bw = new BufferedWriter(new FileWriter("orderlist.txt", true)); // 문서 생성
					bw.append(now + "에 방문한 "); // 문자열 붙임
					bw.append(ordernum + "번 째 손님 주문내역\n");
					bw.append(customer);
					bw.append("> 총 " + payment + "원\n");
					bw.newLine(); // 개행문자
					bw.flush(); // 출력 스트림과 버퍼된 출력 바이트를 강제로 쓰게함
					bw.close(); // 닫기
				} catch (IOException e1) {
					e1.printStackTrace();
				} // 다음 손님의 주문을 받기 위한 초기화 진행
				ta.setText("    상품명          단가         수량        합계\n\n");
				ta.setFont(font1);
				priceTotal.setFont(big);
				priceTotal.setHorizontalAlignment(JTextField.CENTER);
				priceTotal.setText("총 주문 금액 : 0원");
				payment = 0;
				durationTime = 0;
				title.setText(++ordernum + "번째 손님 음식을 주문해주세요!");
				customer = "상품명       수량        금액\n";

			}
		});

		// 초기화 버튼 이벤트
		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < menu.length; i++) { // 모든 메뉴 컴포넌트를 반복을 통해 초기화
					ok[i].setEnabled(false);
					min[i].setEnabled(false);
					su[i].setText("0");
				}
				ta.setText("    상품명          단가         수량        합계\n\n"); // 우측 주문 명세서 초기화
				ta.setFont(font1); // 폰트 지정
				priceTotal.setFont(big); // 폰트 지정
				priceTotal.setHorizontalAlignment(JTextField.CENTER); // 지불가격 위치 중앙에
				priceTotal.setText("총 주문 금액 : 0원"); // 텍스트 설정
				payment = 0;
				durationTime = 0;
			}
		});

		// 직원 호출 버튼 이벤트
		call.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "직원을 호출해드리겠습니다.\n잠시만 기다려 주세요."); // 메세지 다이얼로그 창을 통해 표현
				java.awt.Toolkit.getDefaultToolkit().beep(); // ok버튼을 누르면 부저음 소리가 남
			}
		});

		// 화면 종료 이벤트
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter("orderlist.txt", true));
					bw.append("\n※ 최종 매출액 " + revenue + "원\n");
					bw.newLine();
					bw.flush();
					bw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

	} // 생성자

	@Override
	public void run() {
		super.run();
		while (true) {
			Calendar cal = Calendar.getInstance();
			now = cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE) + "  "
					+ cal.get(Calendar.HOUR) + "시" + cal.get(Calendar.MINUTE) + "분" + cal.get(Calendar.SECOND) + "초";
			clock.setText(now);
			try {
				Thread.sleep(1000); // 1초
			} catch (InterruptedException e) {
				e.printStackTrace();
			} // catch
		} // while문

	}// run메소드

	public static void main(String[] args) {
		Restaurant r = new Restaurant();
		r.start();
	}

}
