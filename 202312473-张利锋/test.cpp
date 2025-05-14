//
// Created by zlf on 2025/5/11.
//
/*
#include "bits/stdc++.h"
using namespace std;
unordered_map<int,vector<int>> m;
vector<int> lu;
int num=0;
bool f= false;
void dfs(int current,int x,vector<bool> v){
    if(current==x){
        f= true;
        return;
    }
    for(int i=0;i<m[current].size();i++){
        int y=m[current][i];
        if(f) {
            return;
        }
        if(v[y]){
            lu.push_back(y);
            v[current]=false;
            dfs(y,x,v);
            lu.pop_back();
            v[current]=true;
        }
    }
}
void dfss(int current,vector<bool> v){
    num++;
    for(int i=0;i<m[current].size();i++){
        int y=m[current][i];
        if(v[y]){
            v[y]=false;
            dfss(y,v);
            v[y]=true;
        }
    }
}
int main(){
    int n,a,b,c,d;
    cin>>n>>a>>b;
    for(int i=0;i<n-1;i++){
        cin>>c>>d;
        m[c].push_back(d);
        m[d].push_back(c);
    }
    vector<bool> v(n+1,true);
    dfs(b,a,v);
    for(int i=0;i<n+1;i++){
        v[i]=true;
    }
    c=lu.size();
    d=lu[(c+1)/2];
    dfss(d,v);
    if(num>=(n+1)/2){
        cout<<"Alice";
    }
    else{
        cout<<"Bob";
    }
}*/
#include "bits/stdc++.h"
using namespace std;
