#include <iostream>
#include <vector>

using namespace std;

/**
	Input 
	n 
	k1 X1[0] X1[1] ...
	k2
	...
	kn
**/

template <typename S> 
class SelectionMap {
private:
	vector<vector<S>> alphabetSets;
	vector<vector<S>*> selectionSet;
	int No;
	int NumOfFields;
	vector<int> kMultiple;
public:
	SelectionMap(const vector<vector<S>> & alphabetSets) {
		this->alphabetSets = alphabetSets;

		this->NumOfFields = alphabetSets.size();
		this->No = 1;

		for (int i)

		selectionSet.resize()
	}
};
int main(void) {
	return 0;
}