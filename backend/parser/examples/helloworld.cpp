#include <iostream>

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
	~helloworld();
	
};

int main()
{
    std::cout << "Hello World!";
    return 0;
}

namespace hello{
	#include <iostream>

	void foo();

	void boo(){

	}
}

void hello::foo(){

}

void helloworld::print() {

}