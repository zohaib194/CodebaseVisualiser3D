#include <iostream>
#include <vector>
#include <unordered_map>

int globalVariable, globalVariable1 = 0;

using namespace std;
class helloworld
{
	class hei
	{
	public:
		hei();
		~hei();

	};
public:
	helloworld();
	void print();
	~helloworld(){
	}

};

helloworld::helloworld(){

}

void lolasdasdasd(){
}
void lol(){

}

int main(int argc, char** argv)
{
	int i;
	int j;
	bool ol;
	bool lo;
    helloworld hello = helloworld();
	hello.print();
	lolasdasdasd();
    std::cout << "Hello World!";
    return 0;
}

// Function pointers are not parsed in current implementation.
//void (*signal(int, void (*fp)(int)))(int);

auto hell() -> bool {
    return true;
}

auto heaven() -> bool {
    return false;
}

int lolasdasdasd(int i, int j, bool istrue, float normalFloat){
	helloworld* hw = new helloworld();

	int z,w,l = 0;

	std::vector<int> vInt;
	std::vector<std::string> vString;
	std::vector<bool> vBool;

	std::unordered_map<std::string, std::string> map;

	// No viable input for line 70 ERROR in .G4 file from antlr.
	//std::unordered_map<int, std::vector<int>> map1;

	hw->print();
	if(true){
		lol();
	}

	return 0;
}


namespace hello{
	#include <iostream>
	int globalVarFromNameSpace;
	int globalInlineVarFromNameSpace, globalInlineVarFromNameSpace1 = 0;

	void foo();
	void foo(int i);
    void foo1(int i){

    }
	void boo(){
		foo();
		foo(1);
		foo1(1);

	}

	auto hoo() -> bool {
	    return false;
	}
}

void hello::foo(){
	lol();
}

void hello::foo(int i){
    lol();
}

void helloworld::print() {

}
