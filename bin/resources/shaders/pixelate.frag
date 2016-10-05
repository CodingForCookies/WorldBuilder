#version 330 compatibility

void main()
{
   	gl_FragColor.rgb = gl_Color.rgb;
   	gl_FragColor.a = 1;
}