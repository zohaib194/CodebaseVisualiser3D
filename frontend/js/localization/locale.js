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
                id: "userinfo_ready_display",
                values: [
                    {
                        language: "english",
                        value: "Ready to display"
                    },
                    {
                        language: "norwegian",
                        value: "Klar for display"
                    }
                ]
            },
            {
                id: "userinfo_read",
                values: [
                    {
                        language: "english",
                        value: "Reading data for organization"
                    },
                    {
                        language: "norwegian",
                        value: "Leser data til organisering"
                    }
                ]
            },
            {
                id: "userinfo_organization",
                values: [
                    {
                        language: "english",
                        value: "Organizing data structures"
                    },
                    {
                        language: "norwegian",
                        value: "Organiserer data strukturer"
                    }
                ]
            },
            {
                id: "userinfo_structure_visualization_assigment",
                values: [
                    {
                        language: "english",
                        value: "Assigning shapes to data structures"
                    },
                    {
                        language: "norwegian",
                        value: "Velger former for data strukturer"
                    }
                ]
            },
            {
                id: "userinfo_websocket_initial_message",
                values: [
                    {
                        language: "english",
                        value: "Received initial request message"
                    },
                    {
                        language: "norwegian",
                        value: "Mottok initial request melding"
                    }
                ]
            },
            {
                id: "userinfo_websocket_initial_message_parsed",
                values: [
                    {
                        language: "english",
                        value: "Parsed"
                    },
                    {
                        language: "norwegian",
                        value: "Lest"
                    }
                ]
            },
            {
                id: "userinfo_websocket_initial_message_skipped",
                values: [
                    {
                        language: "english",
                        value: "Skipped"
                    },
                    {
                        language: "norwegian",
                        value: "Ignorert"
                    }
                ]
            },
            {
                id: "userinfo_websocket_initial_message_failed",
                values: [
                    {
                        language: "english",
                        value: "Failed for repo"
                    },
                    {
                        language: "norwegian",
                        value: "Feilet for brønn"
                    }
                ]
            },
            {
                id: "userinfo_websocket_initial_message_status_finished",
                values: [
                    {
                        language: "english",
                        value: "Done"
                    },
                    {
                        language: "norwegian",
                        value: "Ferdig"
                    }
                ]
            },
            {
                id: "userinfo_websocket_initial_message_status_parsing",
                values: [
                    {
                        language: "english",
                        value: "Parsing"
                    },
                    {
                        language: "norwegian",
                        value: "Leser"
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
                id: "backend_data_not_received",
                values: [
                    {
                        language: "english",
                        value: "Didn't receive data from backend"
                    },
                    {
                        language: "norwegian",
                        value: "Mottok ikke data fra API server"
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
            },
            {
                id: "quality_metric_class_count",
                values: [
                    {
                        language: "english",
                        value: "Number of classes"
                    },
                    {
                        language: "norwegian",
                        value: "Antall klasser"
                    }
                ]
            },
            {
                id: "quality_metric_namespace_count",
                values: [
                    {
                        language: "english",
                        value: "Number of namespaces"
                    },
                    {
                        language: "norwegian",
                        value: "Antall \"namespace\"s"
                    }
                ]
            },
            {
                id: "quality_metric_lines_count",
                values: [
                    {
                        language: "english",
                        value: "Number of code lines found"
                    },
                    {
                        language: "norwegian",
                        value: "Antall linjer med kode funnet"
                    }
                ]
            },
            {
                id: "generic_undefined",
                values: [
                    {
                        language: "english",
                        value: "Variable can't be undefined"
                    },
                    {
                        language: "norwegian",
                        value: "Variabel kan ikke være udefinert"
                    }
                ]
            },
            {
                id: "generic_negative",
                values: [
                    {
                        language: "english",
                        value: "Variable can't be negative"
                    },
                    {
                        language: "norwegian",
                        value: "Variabel kan ikke være negativ"
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
