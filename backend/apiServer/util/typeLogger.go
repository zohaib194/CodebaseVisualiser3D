// Package util contains supplimentary functionality like logging
package util

import (
	"fmt"
	"log"
	"os"
	"path"
	"runtime"
)

// TypeLogger is a wrapper for log that exposes priority based logging functions
var TypeLogger = newTypeLogger()

// logLevel is a type that follows the priorities consts.
type logLevel int

// Identifies the priority of logging from LogDebug as least important to LogError as most important
const (
	logDebug logLevel = iota
	logInfo
	logWarning
	logError
)

// typeLogger wraps a logger and minimum priority level requiered to send content to the logger
type typeLogger struct {
	logger *log.Logger
	level  logLevel
}

// Printout text to identify log importance
const (
	tagDebug   = "[DEBUG]"
	tagInfo    = "[INFO]"
	tagWarning = "[WARNING]"
	tagError   = "[ERROR]"
	tagFatal   = "[FATAL]"
)

// Identifies expected values the environment variable "LOG_LEVEL" can be
const (
	LogDebug   = "LOG_DEBUG"
	LogInfo    = "LOG_INFO"
	LogWarning = "LOG_WARNING"
	LogError   = "LOG_ERROR"
)

// Colors used to make log printout more visilbe
const (
	colorDebug   = "\033[96m"
	colorInfo    = "\033[94m"
	colorError   = "\033[91m"
	colorWarning = "\033[93m"
	colorFatal   = "\033[31m"
	colorWhere   = "\033[95m"
	colorClear   = "\033[00m"
)

// newTypeLogger creates a default logger with Warning as requiered priority to print
func newTypeLogger() typeLogger {
	tLogger := typeLogger{
		level: logWarning,
	}
	tLogger.logger = log.New(os.Stdout, "", log.LstdFlags)

	return tLogger
}

// Debug prints based on a format string if loggers priority is Debug or lower
func (l typeLogger) Debug(format string, v ...interface{}) {
	if l.level <= logDebug {
		message := fmt.Sprintf(format, v...)
		l.logger.Printf(" %s%s%s : %s (%s)", colorDebug, tagDebug, colorClear, message, getCallerPosition())
	}
}

// Info prints based on a format string if loggers priority is Info or lower
func (l typeLogger) Info(format string, v ...interface{}) {
	if l.level <= logInfo {
		message := fmt.Sprintf(format, v...)
		l.logger.Printf(" %s%s%s  : %s (%s)", colorInfo, tagInfo, colorClear, message, getCallerPosition())
	}
}

// Warn prints based on a format string if loggers priority is Warning or lower
func (l typeLogger) Warn(format string, v ...interface{}) {
	if l.level <= logWarning {
		message := fmt.Sprintf(format, v...)
		l.logger.Printf("%s%s%s: %s (%s)", colorWarning, tagWarning, colorClear, message, getCallerPosition())
	}
}

// Error prints based on a format string if loggers priority is Error or lower
func (l typeLogger) Error(format string, v ...interface{}) {
	if l.level <= logError {
		message := fmt.Sprintf(format, v...)
		l.logger.Printf(" %s%s%s : %s (%s)", colorError, tagError, colorClear, message, getCallerPosition())
	}
}

// Fatal prints based on a format string and exits aplication with error code
func (l typeLogger) Fatal(format string, v ...interface{}) {
	message := fmt.Sprintf(format, v...)
	l.logger.Printf(" %s%s%s : %s (%s)", colorFatal, tagFatal, colorClear, message, getCallerPosition())
	os.Exit(1)
}

// SetLogLevel sets the minimal priority a requiered for printout
func SetLogLevel(level string) bool {
	switch level {
	case LogDebug:
		TypeLogger.level = logDebug
		return true

	case LogInfo:
		TypeLogger.level = logInfo
		return true

	case LogWarning:
		TypeLogger.level = logWarning
		return true

	case LogError:
		TypeLogger.level = logError
		return true

	default:
		return false
	}
}

// SetLogFile tries to set a file printout should be sent to returning true or false based on whether it was successfull or not
func SetLogFile(filepath string) bool {
	if len(filepath) == 0 {
		return false
	}
	file, err := os.OpenFile(filepath, os.O_RDWR|os.O_CREATE|os.O_APPEND, 0666)
	if err != nil {
		TypeLogger.Error("Could not open logfile: %v", err)
		return false
	}
	TypeLogger.logger.SetOutput(file)
	return true
}

// getCallerPosition returns the sourcecode file and lineNr where logging was requested
func getCallerPosition() string {
	_, file, line, ok := runtime.Caller(2)
	if !ok {
		return ""
	}
	file = path.Base(file)
	return fmt.Sprintf("%s%s:%d%s", colorWhere, file, line, colorClear)
}
