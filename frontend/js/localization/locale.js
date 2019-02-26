var LOCALE = (function() {
    var locale = {
        sentences: [
            {
                id: "language_not_found",
                values: [
                    {
                        language: "english",
                        value: "Could not find language"
                    },
                    {
                        language: "norwegian",
                        value: "Fant ikke språket"
                    }
                ]
            },
            {
                id: "geometry_invalid_type",
                values: [
                    {
                        language: "english",
                        value: "Unsupported type"
                    },
                    {
                        language: "norwegian",
                        value: "Ugyldig type"
                    }
                ]
            },
            {
                id: "geometry_missing_type",
                values: [
                    {
                        language: "english",
                        value: "Undefined geometry"
                    },
                    {
                        language: "norwegian",
                        value: "Udefinert geometri"
                    }
                ]
            },
            {
                id: "backend_data_received",
                values: [
                    {
                        language: "english",
                        value: "Got the data from backend"
                    },
                    {
                        language: "norwegian",
                        value: "Mottat data fra API server"
                    }
                ]
            },
            {
                id: "displayobject_undefined",
                values: [
                    {
                        language: "english",
                        value: "Undefined DisplayObject"
                    },
                    {
                        language: "norwegian",
                        value: "Udefinert DisplayObject"
                    }
                ]
            },
            {
                id: "fdg_link_missing_parent",
                values: [
                    {
                        language: "english",
                        value: "Parent missing"
                    },
                    {
                        language: "norwegian",
                        value: "Forelder mangler"
                    }
                ]
            },
            {
                id: "fdg_node_exists",
                values: [
                    {
                        language: "english",
                        value: "Already existing node"
                    },
                    {
                        language: "norwegian",
                        value: "Allerede eksiterende node"
                    }
                ]
            },
            {
                id: "theme_invalid_name",
                values: [
                    {
                        language: "english",
                        value: "Invalid theme name"
                    },
                    {
                        language: "norwegian",
                        value: "Invalid stil navn"
                    }
                ]
            },
            {
                id: "quality_metric_funciton_count",
                values: [
                    {
                        language: "english",
                        value: "Nr of functions"
                    },
                    {
                        language: "norwegian",
                        value: "Antall funksjoner"
                    }
                ]
            }
        ]
    };

    // Available languages.
    const languages = ["english", "norwegian"];

    // English language as defualt.
    var currentLanguage = languages[0];

    /**
     * Functions for getting a sentenct based in id.
     * @param {string} id - ID of sentence to fetch.
     */
    var getSentence = function(id) {
        // "id" is undefined or empty, abort.
        if (typeof id === "undefined" || id === ""){
            return;
        }
        
        var result = "";
        
        // Find the sentence from a specific languge.
        // If not found, default to english.
        // If english is not found, give error.
        locale.sentences.forEach((sentence) => {
            if (sentence.id == id) {
                sentence.values.forEach((language) => {
                    if (language.language == currentLanguage) {
                        result = language.value;
                        return;
                    }   
                });

                // Found the sentence in the specified language, return it.
                if (result != "") {
                    return result;
                }
                
                // Defaulting to english language if 
                // wanted language isn't found.
                sentence.values.forEach((language) => {
                    if (language.language == languages[0]) {
                        result = language.value;
                        return;
                    }
                });

                // Found nothing.
                return;
            }
        });

        // Found sentence, returning it.
        if (result != "") {
            return result;
        }

        console.log(getSentence("language_not_found"));
    }
    
    /**
     * Getter for languages.
     */
    var getLanguages = function() {
        // Sort languages alphabetically
        return languages.sort((a, b) => { return a < b; });
    }

    /**
     * Setter for current language to read from.
     */
    var setLanguage = function(name) {
        // If language exists, set it as current.
        var index = languages.indexOf(name);
        if (index >= 0)
            currentLanguage = language[index];

        console.log(getLanguages("theme_invalid_name"));
    }

    // Expose private functions for global use.
    return {
        getSentence: getSentence,
        getLanguages: getLanguages,
        setLanguage: setLanguage
    };
})();
