#mruav2
MRU-A-v2-GitHubRecyclerView

MRU-Av2 GITHUB VIEW (Specs parts 1 - 7)   
---------------------------------------
RecyclerView  
Retrofit  
GsonConverterFactory  

DONE:
- (1,2) Using Retrofit library list all “tetris” Repos by the REST API call:
        https://api.github.com/search/repositories?q=tetris 
- (3,4) RecyclerView grid layout showing Repo name, Owner’s login name and Repo size
- (5) Different background colour for items that have "has_wiki" set to 'true'
- (6) Pagination - Get 10 entries with every REST call and extend the list by 10 more whenever the list is scrolled to the end. Watch out to the rate limit

TODO:
- (7) Add a text field where a user can enter an arbitrary search string instead of "tetris”.

