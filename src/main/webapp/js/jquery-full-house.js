/*
    Based on this script by Marcus Ekwall
    http://jsfiddle.net/mekwall/fNyHs/

	Examples, support and the newest version of this script is here:
	https://github.com/kuchumovn/jquery-full-house

    Author: Nikolay Kuchumov
    github: kuchumovn
    email: kuchumovn@gmail.com
*/

(function($)
{
	var Algorythm = 
	{
		// you can write your own algorythm
		Interface: function(options)
		{
			// called if the 'x' font size is too big, and the text with this font size doesn't fit the container
			this.too_big = function(x) {}
			
			// called if the text with font size 'x' fits the container (e.g. font_size=0 fits any container)
			this.fits = function(x) {}	
			
			// this.retry(x) function will be set automatically
		},
	
		// just for reference
		Linear: function(options)
		{
			var largest_fit = 0
			
			this.too_big = function(x) 
			{
				if (x - 1 === largest_fit)
					return largest_fit
					
				return this.retry(x - 1)
			}
			
			this.fits = function(x) 
			{
				largest_fit = x
				return this.retry(x + 1)
			}
		},
		
		// the faster algorythm
		Binary: function(options)
		{
			var largest_fit
			var minimum_too_big
			
			var step = options.Font_size_increment_step || 10
		
			this.too_big = function(x)
			{
				minimum_too_big = x
				
				if (largest_fit)
				{
					if (largest_fit === x - 1)
						return largest_fit
						
					return this.retry(largest_fit + (x - largest_fit) / 2)
				}
				else
				{
					if (x === 1)
						return 1
						
					return this.retry(x - step)
				}
			}
			
			this.fits = function(x)
			{
				largest_fit = x
				
				if (minimum_too_big)
				{
					if (minimum_too_big === x + 1)
						return x
						
					return this.retry(x + (minimum_too_big - x) / 2)
				}
				else
				{
					return this.retry(x + step)
				}
			}
		}
	}

	function get_initial_font_size(container)
	{
		if (container.css('fontSize'))
		{
			var check = container.css('fontSize').match(/[\d]+px/)
			if (check.length)
				return parseInt(check[0])
		}
		
		return 1
	}
	
	function find_max_font_size(container, options)
	{
		var initial_font_size = get_initial_font_size(container)
		container.css('fontSize', 0)
			
		var html = container.html()
		
		container.empty()
		
		var overflow = container.css('overflow')
		container.css('overflow', 'hidden')
		
		var sandbox = $('<span/>').html(html).appendTo(container)
		
		var available_height = container[0].scrollHeight
		var available_width = container[0].scrollWidth
		
		function try_font_size(font_size)
		{
			container.css({ fontSize: font_size + 'px' })
		}
		
		function recursive_search(algorythm, start_with)
		{
			var find_max_font_size_starting_with = function(font_size)
			{
				font_size = Math.ceil(font_size)
				if (font_size < 1)
					font_size = 1
					
				try_font_size(font_size)
				
				var current_height = container[0].scrollHeight
				var current_width = container[0].scrollWidth
				
				var height_proportion = current_height / available_height
				var width_proportion = current_width / available_width
				
				if (height_proportion > 1 || width_proportion > 1)
					return algorythm.too_big(font_size)
				else
					return algorythm.fits(font_size)
			}
			
			algorythm.retry = find_max_font_size_starting_with
			return find_max_font_size_starting_with(start_with)
		}
		
		options.algorythm = options.algorythm || 'Binary'
		var algorythm = new Algorythm[options.algorythm](options)
		
		var font_size = recursive_search(algorythm, initial_font_size)

		container.css('overflow', overflow)
		container.empty().html(html)
		
		return font_size
	}
	
	$.fn.fill_with_text = function(options)
	{
		options = options || {}
	
		return $(this).each(function()
		{
			var container = $(this)
			container.css({ fontSize: find_max_font_size(container, options) + 'px' })
			if (options.collapse)
				container.css('height', 'auto')
		})
	}
})(jQuery)