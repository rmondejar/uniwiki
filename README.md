# A Reliable and Scalable P2P Wiki Applications

A peer to peer solution for distributing and managing dynamic content, that combines two widely studied technologies: distributed hash tables (DHT) and optimistic replication. On top of a reliable, inexpensive and consistent DHT-based storage, any number of front-ends can be added, ensuring both read and write scalability. 

This implementation is based on a Distributed Interception Middleware, thus separating distribution, replication, and consistency responsibilities, and also making our system usable by third party wiki engines in a transparent way. UniWiki has been proved viable and fairly efficient in large-scale scenarios. 

Website : http://ast-deim.urv.cat/web/software/123-uniwiki

DHT : https://en.wikipedia.org/wiki/Distributed_hash_table
