This is the Capstone Project from the Tim Buchalka's Udemy Course [Java Enterprise Edition 8 for Beginners](https://www.udemy.com/course/java-enterprise-edition-8/) with some additional features:

* Role-based Authorization based on [this stackoverflow answear](https://stackoverflow.com/questions/26777083/best-practice-for-rest-token-based-authentication-with-jax-rs-and-jersey/45814178#45814178)

* JAX-RS Exception Mappers

And some more tests.


# Build

```
mvn clean package && docker build -t academy.learnprogramming/hello-todo .
```

# RUN

```
docker rm -f hello-todo || true && docker run -d -p 8080:8080 -p 4848:4848 --name hello-todo academy.learnprogramming/hello-todo
```