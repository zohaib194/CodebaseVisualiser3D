var STYLE = (function() {
    /**
     * Program style configuration.
     *
     * Avaliabe shapes:
     *     cube, sphere, cylinder, cone, dodecahedron,
     *     icosahedron, octahedron, tetrahedron.
     *
     * Color formats:
     * - Hex: 0x00ff00          // green
     */
    var style = {
        applicationTheme: [
            {
                name: "default",
                colors: {
                    default: 0x000000,
                    background: 0xffffff,
                    ambientLight: 0x808080,
                    light: 0xffffff
                },
                drawables: {
                    function: {
                        shape: "cube",
                        color: 0x00ff00
                    },
                    class: {
                        shape: "cylinder",
                        color: 0xff0000
                    },
                    namespace: {
                        shape: "cone",
                        color: 0x0000ff
                    },
                    link: {
                        shape: null,
                        color: 0xac0000
                    },
                    edge: {
                        shape: null,
                        color: 0x000000
                    }
                }
            },
            {
                name: "sublime",
                colors: {
                    default: 0x000000,
                    background: 0x282923,
                    ambientLight: 0x6d6e6a,
                    light: 0xffffff
                },
                drawables: {
                    function: {
                        shape: "dodecahedron",
                        color: 0xf92472
                    },
                    class: {
                        shape: "dodecahedron",
                        color: 0x67d8ef
                    },
                    namespace: {
                        shape: "dodecahedron",
                        color: 0xac80ff
                    },
                    variable: {
                        shape: "cube",
                        color: 0xfff000
                    },
                    link: {
                        shape: null,
                        color: 0xffffff
                    },
                    edge: {
                        shape: null,
                        color: 0x000000
                    }
                }
            },
            {
                name: "code_vis",
                colors: {
                    default: 0x000000,
                    background: 0xffffff,
                    ambientLight: 0xdfd8c5,
                    light: 0xffffff
                },
                drawables: {
                    function: {
                        shape: "cube",
                        color: 0x00ff00
                    },
                    class: {
                        shape: "cylinder",
                        color: 0xf58025
                    },
                    namespace: {
                        shape: "cone",
                        color: 0xad208e
                    },
                    variable: {
                        shape: "cube",
                        color: 0xad208e
                    },
                    link: {
                        shape: null,
                        color: 0x552988
                    },
                    edge: {
                        shape: null,
                        color: 0x000000
                    }
                }
            }
        ],
        graphics: {
            camera: {
                fov: 45,
                nearPlane: 0.1,
                farPlane: 9001
            }
        }
    };

    var currentApplicationTheme = 1;

    /**
     * Getter for currently active application theme's name.
     */
    var getName = function() {
        return style.applicationTheme[currentApplicationTheme].name;
    }

    /**
     * Getter for currently active application theme's color.
     */
    var getColors = function() {
        return style.applicationTheme[currentApplicationTheme].colors;
    }

    /**
     * Getter for currently active application theme's drawables.
     */
    var getDrawables = function() {
        return style.applicationTheme[currentApplicationTheme].drawables;
    }

    /**
     * Getter for currently active styles' graphics settings.
     */
    var getGraphics = function() {
        return style.graphics;
    }

    /**
     * Getter for available application themes.
     */
    var getThemes = function() {
        var names = [];
        style.applicationTheme.forEach((theme) => {
            names.push(style.applicationTheme[i].name);
        });
        // Sort names alphabetically.
        names.sort((a, b) => { return a < b; });
        return names;
    }

    /**
     * Setter for currently active application theme.
     * @param {string} name - name of application theme.
     */
    var setCurrentTheme = function(name) {
        style.applicationTheme.forEach((theme) => {
            if (theme.name === name) {
                currentApplicationTheme = index;
                return;
            }
        });

        console.log(LOCALE.getSentence("theme_find_failed"));
    }

    // Expose private functions for global use.
    return {
        getName: getName,
        getColors: getColors,
        getDrawables: getDrawables,
        getGraphics: getGraphics,
        getThemes: getThemes,
        setCurrentTheme: setCurrentTheme
    };
})();