# gen

## setup

- install java 9

- install boot

```bash
sudo bash -c "cd /usr/local/bin && curl -fsSLo boot https://github.com/boot-clj/boot-bin/releases/download/latest/boot.sh && chmod 755 boot"
```

- set env var for java 9 interop

```bash
export BOOT_JVM_OPTIONS='--add-modules "java.xml.bind"'
```

## try boot

Boot runs tasks. See tasks that can be run

```
$ boot -h
```

E.g. `cljs`

```
$ boot cljs
```

Use with predefined `target` task to write output files

```
$ boot cljs target
```

See the output folder target appear e.g. with `tree`

```
$ tree

.
|-- build.boot
|-- html
|   `-- index.html
|-- readme.md
|-- src
|   `-- cljs
|       `-- gen
|           `-- core.cljs
`-- target
    |-- index.html
    |-- main.js
    `-- main.out
        ...

```

Serve the target folder with the `serve` task, use `wait` to make it not quit immediately

```
boot wait serve -d target
```

Use predefined `watch` task to recompile on change (watch also `wait`s so we can skip it)

```
boot serve -d target watch cljs target
```

Use `reload` task to reload the browser on recompile

```
boot serve -d target watch reload cljs target
```

Edit `src/cljs/gen/core.cljs` to see the browser recompile on change

## compose boot tasks into custom tasks

See `build.boot` to find the `dev` tasks which is the same as the above

```
boot dev
```