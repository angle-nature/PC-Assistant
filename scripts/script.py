import sys
sys.stdout.reconfigure(encoding='utf-8')
import wmi

c = wmi.WMI(namespace='wmi')
methods = c.WmiMonitorBrightnessMethods()[0]
methods.WmiSetBrightness(0, 0)