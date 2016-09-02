import java.util.Scanner;
import java.io.BufferedReader;
//import java.io.File;
import java.io.FileNotFoundException;
//import java.io.FileReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class simulator {
	static int [] registers = new int [33];
	static int [] reg_valid = new int [33];
	static int [] memory = new int[10000];
	static int prog_counter=20000;
	static int X;
	static int cycles_completed = 0;
	static ArrayList<String> instruction = new ArrayList<String>();
	
	public static void main(String args[]) throws IOException{
	//String filename = args[0];
	int ch, num_cycle;
	
	while(true){
		
		System.out.println("\n1:Load");
		System.out.println("2:Initialize");
		System.out.println("3:Simulate");
		System.out.println("4:Display");
		System.out.println("5:Quit");
		System.out.println("Enter choice: ");
		Scanner sc = new Scanner(System.in);
		ch = sc.nextInt();
		switch(ch){
			case 1:
					//FileInputStream fis = null;
					//BufferedReader reader = null;
					
					try{
						BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
						instruction = new ArrayList<String>();
						System.out.println("Enter filename: ");
						String s  = reader1.readLine();
						s = s.replaceAll(" ","");
					
						FileInputStream fis = new FileInputStream(s);
						BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
						String strLine;
						while((strLine=reader.readLine())!= null){
							strLine = strLine.replaceAll(","," ");
							strLine = strLine.replaceAll("#", " ");
							strLine = strLine.replaceAll("  ", " ");
							instruction.add(strLine);
							
						}
						
						System.out.println("File Loaded Successfully");
						reader.close();
						
					}catch(FileNotFoundException ex){
						ex.printStackTrace();
					}
					break;
			case 2: 
					initialize();
					break;
			case 3: 
					System.out.println("Enter number of cycles: ");
					num_cycle = sc.nextInt();
					simulate(num_cycle);
					break;
					
			case 4: display();
					break;
			case 5: System.exit(0);
					break;
		}
	//sc.close();
	}
	
}
	
	public static void initialize(){
		//instruction = new ArrayList<String>();
		
		cycles_completed = 0; 
		for(int i=0;i<33;i++){
			registers[i] = 0;
			reg_valid[i] = 1;	
		}
		for(int i=0;i<10000;i++){
			memory[i] = 0;
		}
		
		prog_counter = 20000;
		f_pc = -1;d_pc = -1;e_pc = -1;m_pc = -1;w_pc = -1;
		f_ins= "";d_ins= "";e_ins= "";m_ins= "";w_ins= "";
		e_res=-1;m_res = -1;w_res = -1;
		System.out.println("Initialized!!!");
	}
	
	public static void display(){
		System.out.println("-------------------------------------");
		System.out.println("Program counter in Fetch:"+(f_pc+20000));
		System.out.println("Instruction in fetch:"+f_ins);
		
		System.out.println("Program counter in Decode:"+(d_pc+20000));
		System.out.println("Instruction in Decode:"+d_ins);
	
		System.out.println("Program counter in Execute:"+(e_pc+20000));
		System.out.println("Instruction in Execute:"+e_ins);
		
		System.out.println("Program counter in Memory:"+(m_pc+20000));
		System.out.println("Instruction in Memory:"+m_ins);
		
		System.out.println("Program counter in WriteBack:"+(w_pc+20000));
		System.out.println("Instruction in Writeback:"+w_ins);
		
		System.out.println();
		
		for(int i=0; i<32; i++){
			System.out.print("R["+i+"]:"+registers[i]+ "\t");
		}
		
		System.out.print("X"+":"+registers[32]+"\t");
		
		System.out.println("\n");
		
		for(int i=0; i<100; i++){
			System.out.print("MEM["+i+"]:"+memory[i]+ " \t");
		}
		System.out.println("\n------------------------------------------------------------------\n");
		System.out.println("Number of cycles completed:" +cycles_completed);
		
	}
	
	public static void simulate(int num_cycle){
		for(int i=0; i<num_cycle;i++){
			writeback();
			mem();
			execute();
			decode();
			fetch();
			cycles_completed++;
			
		}
		
		//System.out.println("Number of cycles:" +num_cycle);
	}

	static int f_pc;
	static String f_ins;
	public static void fetch(){
		f_pc = prog_counter-20000;
		if(f_pc > instruction.size()-1){
			f_ins = "NOP";
		}
		else{
		f_ins = instruction.get(f_pc);
		}
		if(d_flag==0){
			prog_counter++;
		}
		else{
			//Do nothing
		}
	}
	
	static int d_pc;
	static String d_ins;
	static int d_flag = 0; //1 for halt
	static int d_flag1 = 0;
	static int b_flag = 0;
	public static void decode(){
		d_flag1 = 0;
		
		if(d_flag==0){
			d_pc = f_pc;
			d_ins = f_ins;
		}
		
		else{
			//do nothing
		}
		d_flag  = 0;
		
		if(b_flag ==1){
			d_ins = "";
			b_flag = 0;
		}
		String[] type_ins = d_ins.split(" ");
		
		if(d_ins.equals("")){
			//do nothing
		}
		
		else if(type_ins[0].compareTo("BAL")==0){
			//prog_counter = Integer.parseInt(type_ins[1]);
			//registers[32] = X;
			X = d_pc+20000 + 1;
			reg_valid[32] = 0; //set invalid
			b_flag = 1;
			
		}
		else if(type_ins[0].compareTo("BZ")==0){
			int pc_prev = d_pc -1;
			String prev = instruction.get(pc_prev);
			String [] temp = prev.split(" ");
			int index = Integer.parseInt(temp[1].substring(1));
			if(reg_valid[index]==1){
				if(registers[index]==0){
					//prog_counter = d_pc + Integer.parseInt(type_ins[1]) + 20000;
					b_flag = 1;
				}
			}
			else{
				d_flag = 1;
			}
		}
		else if(type_ins[0].compareTo("BNZ")==0){
			int pc_prev = d_pc -1;
			String prev = instruction.get(pc_prev);
			String [] temp = prev.split(" ");
			int index = Integer.parseInt(temp[1].substring(1));
			if(reg_valid[index]==1){
				if(registers[index]!=0){
					//prog_counter = d_pc + Integer.parseInt(type_ins[1]) + 20000;
					b_flag = 1;
				}
			}
			else{
				d_flag = 1;
			}
		}
		else if(type_ins[0].compareTo("JUMP")==0){
			//prog_counter = registers[32];
			b_flag = 1;
		}
		else{
			if(type_ins[0].compareTo("HALT")==0){
				d_flag = 1;
			}
			else if(type_ins[0].compareTo("NOP")==0){
				
			}
			else if(type_ins[0].compareTo("MOVC")==0){
				int reg_number = Integer.parseInt(type_ins[1].substring(1));
				if(reg_valid[reg_number]==0){
					d_flag = 1;
				}
				else{
					d_flag = 0;
					reg_valid[reg_number]=0;
				}
				
			}
			else{
				int reg_number = Integer.parseInt(type_ins[1].substring(1));
				if(reg_valid[reg_number]==0){
					d_flag1 = 1;
				}
				else{
					
					//d_flag = 0;
//					if(type_ins[0].compareTo("STORE")!=0)
//						reg_valid[reg_number] = 0;
				}
				int reg_number1 = Integer.parseInt(type_ins[2].substring(1));
				if(reg_valid[reg_number1]==0){
					d_flag1 = 1;
				}
				else{
					//d_flag = 0;
					//reg_valid[reg_number1] = 0;
				}
				if(type_ins[3].contains("R")){
					int reg_number2 = Integer.parseInt(type_ins[3].substring(1));
					if(reg_valid[reg_number2]==0){
						d_flag1 = 1;
					}
					else{
						//d_flag = 0;
						//reg_valid[reg_number2] = 0;
					}
				}
				if(d_flag1==1){
					d_flag = 1;
				}
				else{
					d_flag = 0;
					//int reg_number = Integer.parseInt(type_ins[1].substring(1));
					if(type_ins[0].compareTo("STORE")!=0)
						reg_valid[reg_number] = 0;
				}
			}
			
		}
		
	}
	
	static int e_pc;
	static String e_ins;
	static int e_res;
	public static void execute(){
		e_ins="";
		if(d_flag==1){
			e_ins = "";
			//System.out.println("Debug:Bubble");
		}
		else{
			e_pc = d_pc;
			e_ins = d_ins;
			String[] type_ins = e_ins.split(" ");
			if(type_ins[0].compareTo("ADD")==0){
				int src1,src2;
				if(type_ins[2].contains("R")){
					src1 = registers[Integer.parseInt(type_ins[2].substring(1))];
				}
				else{
					src1=Integer.parseInt(type_ins[2]);
				}
				if(type_ins[3].contains("R")){
					src2 = registers[Integer.parseInt(type_ins[3].substring(1))];
				}
				else{
					src2 = Integer.parseInt(type_ins[3]);
				}
				e_res = src1+src2;
				
			}
			else if(type_ins[0].compareTo("SUB")==0){
				int src1,src2;
				if(type_ins[2].contains("R")){
					src1 = registers[Integer.parseInt(type_ins[2].substring(1))];
				}
				else{
					src1=Integer.parseInt(type_ins[2]);
				}
				if(type_ins[3].contains("R")){
					src2 = registers[Integer.parseInt(type_ins[3].substring(1))];
				}
				else{
					src2=Integer.parseInt(type_ins[3]);
				}
				e_res = src1-src2;
			}
			else if(type_ins[0].compareTo("AND")==0){
				int src1,src2;
				if(type_ins[2].contains("R")){
					src1 = registers[Integer.parseInt(type_ins[2].substring(1))];
				}
				else{
					src1=Integer.parseInt(type_ins[2]);
				}
				if(type_ins[3].contains("R")){
					src2 = registers[Integer.parseInt(type_ins[3].substring(1))];
				}
				else{
					src2=Integer.parseInt(type_ins[3]);
				}
				e_res = src1&src2;
			}
			else if(type_ins[0].compareTo("OR")==0){
				int src1,src2;
				if(type_ins[2].contains("R")){
					src1 = registers[Integer.parseInt(type_ins[2].substring(1))];
				}
				else{
					src1=Integer.parseInt(type_ins[2]);
				}
				if(type_ins[3].contains("R")){
					src2 = registers[Integer.parseInt(type_ins[3].substring(1))];
				}
				else{
					src2=Integer.parseInt(type_ins[3]);
				}
				e_res = src1|src2;
			}
			else if(type_ins[0].compareTo("XOR")==0){
				int src1,src2;
				if(type_ins[2].contains("R")){
					src1 = registers[Integer.parseInt(type_ins[2].substring(1))];
				}
				else{
					src1=Integer.parseInt(type_ins[2]);
				}
				if(type_ins[3].contains("R")){
					src2 = registers[Integer.parseInt(type_ins[3].substring(1))];
				}
				else{
					src2=Integer.parseInt(type_ins[3]);
				}
				e_res = src1^src2;
			}
			else if(type_ins[0].compareTo("MUL")==0){
				int src1,src2;
				if(type_ins[2].contains("R")){
					src1 = registers[Integer.parseInt(type_ins[2].substring(1))];
				}
				else{
					src1=Integer.parseInt(type_ins[2]);
				}
				if(type_ins[3].contains("R")){
					src2 = registers[Integer.parseInt(type_ins[3].substring(1))];
				}
				else{
					src2=Integer.parseInt(type_ins[3]);
				}
				e_res = src1*src2;
			}
			else if(type_ins[0].compareTo("MOVC")==0){
				
				e_res = Integer.parseInt(type_ins[2]);
			}
			else if(type_ins[0].compareTo("LOAD")==0){
				int src1,src2;
				if(type_ins[2].contains("R")){
					src1 = registers[Integer.parseInt(type_ins[2].substring(1))];
				}
				else{
					src1=Integer.parseInt(type_ins[2]);
				}
				if(type_ins[3].contains("R")){
					src2 = registers[Integer.parseInt(type_ins[3].substring(1))];
				}
				else{
					src2=Integer.parseInt(type_ins[3]);
				}
				e_res = src1+src2;
			}
			else if(type_ins[0].compareTo("STORE")==0){
				int src1,src2;
				if(type_ins[2].contains("R")){
					src1 = registers[Integer.parseInt(type_ins[2].substring(1))];
				}
				else{
					src1=Integer.parseInt(type_ins[2]);
				}
				if(type_ins[3].contains("R")){
					src2 = registers[Integer.parseInt(type_ins[3].substring(1))];
				}
				else{
					src2=Integer.parseInt(type_ins[3]);
				}
				e_res = src1+src2;
			}
			else if(type_ins[0].compareTo("BAL")==0){
				//f_pc = prog_counter;
				prog_counter = Integer.parseInt(type_ins[1]);
				
			}
			else if(type_ins[0].compareTo("JUMP")==0){
				prog_counter = registers[32];
			}
			else if(type_ins[0].compareTo("BNZ")==0){
				//prog_counter = e_pc + Integer.parseInt(type_ins[1]) + 20000;
				int pc_prev = e_pc -1;
				String prev = instruction.get(pc_prev);
				String [] temp = prev.split(" ");
				int index = Integer.parseInt(temp[1].substring(1));
				if(reg_valid[index]==1){
					if(registers[index]!=0){
						prog_counter = e_pc + Integer.parseInt(type_ins[1]) + 20000;
						//b_flag = 1;
					}
				}
				else{
					//d_flag = 1;
				}
			}
			else if(type_ins[0].compareTo("BZ")==0){
				//prog_counter = e_pc + Integer.parseInt(type_ins[1]) + 20000;
				int pc_prev = e_pc -1;
				String prev = instruction.get(pc_prev);
				String [] temp = prev.split(" ");
				int index = Integer.parseInt(temp[1].substring(1));
				if(reg_valid[index]==1){
					if(registers[index]==0){
						prog_counter = e_pc + Integer.parseInt(type_ins[1]) + 20000;
						//b_flag = 1;
					}
				}
				else{
					//d_flag = 1;
				}
			
			}
			
		}
	}
	
	static int m_pc;
	static String m_ins;
	static int m_res;
	public static void mem(){
		m_pc = e_pc;
		m_ins = e_ins;
		m_res = e_res;
		String[] type_ins = m_ins.split(" ");
		
		if(type_ins[0].compareTo("LOAD")==0){
			m_res = memory[m_res];
			
		}
	
		if(type_ins[0].compareTo("STORE")==0){
			memory[m_res] = registers[Integer.parseInt(type_ins[1].substring(1))];
			
		}
	}
	
	static int w_pc;
	static String w_ins;
	static int w_res;
	public static void writeback(){
		w_pc = m_pc;
		w_ins = m_ins;
		w_res = m_res;
		
		String[] type_ins = w_ins.split(" ");
		
		if((type_ins.length==3)|| (type_ins.length==4 && type_ins[0].compareTo("STORE") != 0) ){
			registers[Integer.parseInt(type_ins[1].substring(1))] = w_res;
			reg_valid[Integer.parseInt(type_ins[1].substring(1))] = 1;
			
			
//System.out.println(Integer.parseInt(type_ins[1].substring(1))+":::"+reg_valid[Integer.parseInt(type_ins[1].substring(1))]);
		}
		if(type_ins[0].compareTo("BAL") == 0){
			registers[32] = X;
			reg_valid[32] = 1;
		}
	
	}
		
}
